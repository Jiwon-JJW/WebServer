package handler;

import annotation.Controller;
import annotation.GetMapping;
import annotation.PostMapping;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Set;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;
    public Class<?> targetClass;
    public BeanFactory beanFactory;

    public RequestHandler(Socket connectionSocket, BeanFactory beanFactory) {
        this.connection = connectionSocket;
        this.beanFactory = beanFactory;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            DataOutputStream dos = new DataOutputStream(out);

            String[] startLine = bufferedReader.readLine().split(" ");
            String method = startLine[0];
            String url = startLine[1];

            Method controllerMethod = findControllerMethod(method, url);
            if(controllerMethod != null) {
                // Invoke 에 대한 설명
                // 컨트롤러 안의 메소드를 무작정 들어가 결과값을 도출해오는것.
                // https://bbaddoroid.tistory.com/6
                // getConstructor().newInstance()
                // 새로운 Object 객체 생성
                Object response = controllerMethod.invoke(targetClass.getConstructor().newInstance());
                System.out.println("./webapp" + response.toString());
                byte[] body = Files.readAllBytes(new File("./webapp" + response.toString()).toPath());

                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
            log.error(e.getMessage());
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body,0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public Method findControllerMethod(String method, String url) throws ClassNotFoundException {
        // Reflections 클래스는 원하는 클래스를 찾기 위해 사용
        // 파라미터값은 클래스를 찾을때 출발 패키지
        // beanClasses 내의 클래스들을 탐색
        Reflections reflections = new Reflections(beanFactory.beanClasses);

        // getTypesAnnotatedWith():
        // 파라미터값으로 넘긴 어노테이션이 붙은 클래스를 찾는다.
        // 반환값: @Controller 어노테이션이 선언된 클래스 목록
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Controller.class);
        log.info("Controller classes : {}", classes);

        for(Class<?> c : classes) {
            // 클래스 내의 메소드를 찾는다.
            Method controllerMethod = findControllerClass(c, method, url);

            if(controllerMethod != null) {
                targetClass = c;
                log.info("Controller classes : {}", controllerMethod);
                return controllerMethod;
            }
        }
        return null;
    }

    public static Method findControllerClass(Class<?> controller, String method, String url) {
        Method[] methods = controller.getMethods();

        for (Method m : methods) {
            //  어노테이션이 존재하는지 체크한다.
            if (m.isAnnotationPresent(GetMapping.class) && method.equals("GET")) {
                // annotation.GetMapping 어노테이션을 취득
                // 출처: https://nowonbun.tistory.com/530
                GetMapping getMapping = m.getDeclaredAnnotation(GetMapping.class);
                if (getMapping.url().equals(url)) {
                    return m;
                }
            }
            if (m.isAnnotationPresent(PostMapping.class) && method.equals("POST")) {
                PostMapping getMapping = m.getDeclaredAnnotation(PostMapping.class);
                if (getMapping.url().equals(url)) {
                    return m;
                }
            }
        }
        return null;
    }
}
