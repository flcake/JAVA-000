package greekTime.week1;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，
 * 此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件
 */
public class MyClassLoader extends ClassLoader {
    public static void main(String[] args) {
        try {
            Object obj = new MyClassLoader().findClass("Hello").newInstance();
            Method method = obj.getClass().getMethod("hello");
            method.invoke(obj);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Class<?> findClass(String name) {
        File classFile = new File("E:\\work\\learn\\src\\main\\greekTime\\week1\\Hello.xlass");
        try {
            //class 文件读入数组
            byte[] array = FileUtils.readFileToByteArray(classFile);

            //处理二进制文件
            for (int i = 0; i < array.length; i++) {
                array[i] = decode(array[i]);
            }
            return defineClass(name, array, 0, array.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte decode(byte b) {
        return (byte)(255 - b);
    }
}
