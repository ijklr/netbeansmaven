package com.mycompany.netbeansmaven;

import java.util.Optional;

/**
 *
 * @author scheng
 */
public class JavaLearning {
    
    public static void main( String[] args ) {
        System.out.println("hello ");
        Optional<String> opt = Optional.of("hey...");
        boolean b = opt.isEmpty();
        System.out.println("b===" + b);
    }
}
