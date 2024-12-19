package aaadddfgh.myapp.AItiaotiao.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassFactory {

    private Object instance;

    public ClassFactory(String packageName, String className, Object... constructorArgs) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // 拼接完整类名
        String fullClassName = packageName + "." + className;

        // 加载类
        Class<?> clazz = Class.forName(fullClassName);

        // 获取指定的有参构造函数
        Constructor<?> constructor = getConstructor(clazz, constructorArgs);

        // 创建实例
        instance = constructor.newInstance(constructorArgs);
    }

    private Constructor<?> getConstructor(Class<?> clazz, Object[] constructorArgs) throws NoSuchMethodException {
        Class<?>[] parameterTypes = new Class[constructorArgs.length];
        for (int i = 0; i < constructorArgs.length; i++) {
            parameterTypes[i] = constructorArgs[i].getClass();
        }

        // 获取指定的有参构造函数
        return clazz.getConstructor(parameterTypes);
    }

    public Object getInstance() {
        return instance;
    }

    /**
     * 调用实例的方法，并传递不定数量的参数。
     *
     * @param methodName 方法名
     * @param args 参数数组
     * @return 方法调用的结果
     * @throws NoSuchMethodException 如果找不到指定的方法
     * @throws IllegalAccessException 如果无法访问方法
     * @throws InvocationTargetException 如果方法调用时发生异常
     */
    public Object invokeMethod(String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // 获取方法的参数类型数组
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }

        // 获取指定的方法
        Method method = instance.getClass().getMethod(methodName, parameterTypes);

        // 调用方法并返回结果
        return method.invoke(instance, args);
    }

    public static void main(String[] args) {
        try {
            // 示例用法
            ClassFactory factory = new ClassFactory("com.example", "MyClass", "Hello", 123);
            Object myInstance = factory.getInstance();

            // 假设 MyClass 有一个方法名为 `myMethod`，接收两个参数：String 和 int
            String result = (String) factory.invokeMethod("myMethod");
            System.out.println(result);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}