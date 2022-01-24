package handler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BeanFactory {
    ConcurrentMap<String, Object> beanFactory = new ConcurrentHashMap<>();
    List<Class<?>> beanClasses = new ArrayList<>();

    public BeanFactory() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Handler에 있는 findComponentClass로 Component 어노테이션이 붙은 클래스들을 찾아내고 List에 등록
        beanClasses = Handler.findComponentClass();
        initializeBean();
    }

    private void initializeBean() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for(Class<?> beanClass: beanClasses) {
            // beanClass의 생성자를 호출해서 새로운 인스턴스를 생성함.
            Object beanInstance = beanClass;
            String beanClassName = beanClass.getName();
            beanFactory.putIfAbsent(beanClassName, beanInstance);
        }
    }

    public Object getBean(String beanName) throws ClassNotFoundException {
        if(!beanFactory.containsKey(beanName)) {
            throw new ClassNotFoundException();
        }
        return beanFactory.get(beanName);
    }

    public Object getBean(Class<?> type) throws ClassNotFoundException {
        for (Object bean : beanFactory.values()) {
            if(type.getName().equals(bean.getClass().getName())) {
                return bean;
            }
        }

        throw new ClassNotFoundException();
    }
}
