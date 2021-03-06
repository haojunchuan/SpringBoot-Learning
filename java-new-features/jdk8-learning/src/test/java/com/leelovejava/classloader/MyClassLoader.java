package com.leelovejava.classloader;

import java.io.*;

/**
 * 自定义类加载器
 * 自定义类加载器的核心在于对字节码文件的获取，如果是加密的字节码则需要在该类中对文件进行解密
 * @author leelovejava
 */
public class MyClassLoader extends ClassLoader {

    private String root;

    protected Class<?> findClass(String name) throws ClassNotFoundException {

        byte [] classData = loadClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            return defineClass(name, classData, 0, classData.length);
        }

    }

    private byte[] loadClassData(String className) {
        String fileName = root + File.separatorChar
                + className.replace('.', File.separatorChar) + ".class";

        try {
            InputStream ins = new FileInputStream(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length;
            while ((length = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getRoot() {
        return root;

    }

    public void setRoot(String root) {
        this.root = root;

    }

    public static void main(String[] args) {
        // 1、这里传递的文件名需要是类的全限定性名称，即com.paddx.test.classloading.Test格式的，因为 defineClass 方法是按这种格式进行处理的。
        MyClassLoader classLoader = new MyClassLoader();
        classLoader.setRoot("E:\\temp");
        Class<?> testClass;

        try {
            // 2、最好不要重写loadClass方法，因为这样容易破坏双亲委托模式。
            testClass = classLoader.loadClass("com.neo.classloader.Test2");
            Object object = testClass.newInstance();
            System.out.println(object.getClass().getClassLoader());
            // 3、这类Test 类本身可以被AppClassLoader类加载，因此我们不能把com/paddx/test/classloading/Test.class放在类路径下。否则，由于双亲委托机制的存在，会直接导致该类由AppClassLoader加载，而不会通过我们自定义类加载器来加载。
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}