package handler;

import annotation.Component;
import annotation.Controller;
import annotation.GetMapping;
import annotation.PostMapping;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    public static Class<?> targetClass;

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException {

        Scanner scanner = new Scanner(System.in);
        String request;
        BeanFactory beanFactory = new BeanFactory();

        while (!((request = scanner.nextLine()).equals("exit"))) {
            String[] startLine = request.split(" ");
            String method = startLine[0];
            String url = startLine[1];

            Method controllerMethod = findControllerMethod(beanFactory, method, url);
            if(controllerMethod != null) {
                // Invoke 에 대한 설명
                // 컨트롤러 안의 메소드를 무작정 들어가 결과값을 도출해오는것.
                // https://bbaddoroid.tistory.com/6
                // getConstructor().newInstance()
                // 새로운 Object 객체 생성
                Object response = controllerMethod.invoke(targetClass.getConstructor().newInstance());
                System.out.println(response.toString());
            }

            scanner = new Scanner(System.in);
        }
    }
    public static ArrayList<Class<?>> findComponentClass() throws ClassNotFoundException {
        // Reflections 클래스는 원하는 클래스를 찾기 위해 사용
        // 파라미터값은 클래스를 찾을때 출발 패키지
        // "" -> classpath 모든 패키지 검색
        Reflections reflections = new Reflections("");

        // getTypesAnnotatedWith():
        // 파라미터값으로 넘긴 어노테이션이 붙은 클래스를 찾는다.
        // 반환값: @Component 어노테이션이 선언된 클래스 목록
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);
        log.info("Bean classes : {}", classes);
        return new ArrayList<>(classes);
    }

    public static Method findControllerMethod(BeanFactory beanFactory, String method, String url) throws ClassNotFoundException {
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
