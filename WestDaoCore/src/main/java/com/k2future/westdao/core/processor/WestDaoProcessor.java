package com.k2future.westdao.core.processor;

import com.google.auto.service.AutoService;
import com.k2future.westdao.core.annotations.WestDao;
import com.k2future.westdao.core.handler.OperationManager;
import com.squareup.javapoet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes("com.k2future.westdao.core.annotations.WestDao")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class WestDaoProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(WestDao.class)) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                WestDao westDao = typeElement.getAnnotation(WestDao.class);
                String prefix = formatPrefix(westDao.prefix());
                try {
                    generateMethods(typeElement, prefix);
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Failed to generate methods: " + e.getMessage());
                }
            }
        }
        return true;
    }

    private String formatPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("@WestDao.prefix must not be empty");
        }
        return Character.toUpperCase(prefix.charAt(0)) + prefix.substring(1);

    }

    private void generateMethods(TypeElement typeElement, String prefix) throws IOException {
        String className = typeElement.getSimpleName().toString();
        String newClassName = prefix + className;

        ClassName parentClassName = ClassName.get(processingEnv.getElementUtils().getPackageOf(typeElement).toString(), className);
        ClassName childClassName = ClassName.get(processingEnv.getElementUtils().getPackageOf(typeElement).toString(), newClassName);
        ParameterizedTypeName interfaceType = ParameterizedTypeName.get(
                ClassName.get("com.k2future.westdao.core.handler", "WestDao"),
                parentClassName
        );

        TypeSpec.Builder builder = TypeSpec.classBuilder(newClassName)
                .superclass(parentClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(interfaceType)
                .addAnnotation(Component.class);


        FieldSpec entityManager = FieldSpec.builder(OperationManager.class, "operationManager", Modifier.PRIVATE, Modifier.STATIC)
                .build();
        MethodSpec setOperationManager = MethodSpec.methodBuilder("setOperationManager")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Autowired.class)
                .addStatement(newClassName + ".operationManager = operationManager")
                .addParameter(OperationManager.class, "operationManager")
                .build();


        MethodSpec.Builder parseParentBuilder = MethodSpec.methodBuilder("parseParent")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(parentClassName)
                .addParameter(childClassName, "child")
                .addStatement(String.format("%s parent = new %s()", className, className));

        // Add setter calls for fields
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement instanceof VariableElement) {
                VariableElement field = (VariableElement) enclosedElement;
                String fieldName = field.getSimpleName().toString();
                String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                String getterMethodName = "get" + capitalizedFieldName;
                String setterMethodName = "set" + capitalizedFieldName;
                parseParentBuilder.addStatement(String.format("parent.%s(child.%s())", setterMethodName, getterMethodName));
            }
        }
        parseParentBuilder.addStatement("return parent");
        MethodSpec parseParentMethod = parseParentBuilder.build();

        // save
        MethodSpec saveMethod = MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC)
                .returns(parentClassName)
                .addAnnotation(Override.class)
                .addStatement(String.format("return %s.operationManager.<%s>save(%s.parseParent(this))", newClassName, className, newClassName))
                .build();
        // delete
        MethodSpec deleteByMethod = MethodSpec.methodBuilder("deleteById")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addAnnotation(Override.class)
                .addStatement(String.format("return %s.operationManager.<%s>deleteById(%s.parseParent(this))", newClassName, className, newClassName))
                .build();
        // delete
        MethodSpec deleteAllMethod = MethodSpec.methodBuilder("deleteAll")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addAnnotation(Override.class)
                .addStatement(String.format("return %s.operationManager.<%s>deleteAll(%s.parseParent(this))", newClassName, className, newClassName))
                .build();

        // findById
        MethodSpec findByIdMethod = MethodSpec.methodBuilder("findById")
                .addModifiers(Modifier.PUBLIC)
                .returns(parentClassName)
                .addAnnotation(Override.class)
                .addStatement(String.format("return %s.operationManager.<%s>findById(%s.parseParent(this))", newClassName, className, newClassName))
                .build();

        // updateById
        MethodSpec updateByIdMethod = MethodSpec.methodBuilder("updateById")
                .addModifiers(Modifier.PUBLIC)
                .returns(parentClassName)
                .addAnnotation(Override.class)
                .addStatement(String.format("return %s.operationManager.<%s>updateById(%s.parseParent(this))", newClassName, className, newClassName))
                .build();

        // findAll
        MethodSpec findAllMethod = MethodSpec.methodBuilder("findAll")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get("java.util", "List"), parentClassName))
                .addAnnotation(Override.class)
                .addStatement(String.format("return %s.operationManager.<%s>findAll(%s.parseParent(this))", newClassName, className, newClassName))
                .build();

        // findOne method
        MethodSpec findOneMethod = MethodSpec.methodBuilder("findOne")
                .addModifiers(Modifier.PUBLIC)
                .returns(parentClassName)
                .addAnnotation(Override.class)
                .addStatement(String.format("return %s.operationManager.<%s>findOne(%s.parseParent(this))", newClassName, className, newClassName))
                .build();
        // page
        MethodSpec pageMethod = MethodSpec.methodBuilder("page")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get("org.springframework.data.domain", "Page"), parentClassName))
                .addAnnotation(Override.class)
                .addParameter(ClassName.get("org.springframework.data.domain", "Pageable"), "page")
                .addStatement(String.format("return %s.operationManager.<%s>page(%s.parseParent(this), page)", newClassName, className, newClassName))
                .build();
        // count
        MethodSpec countMethod = MethodSpec.methodBuilder("count")
                .addModifiers(Modifier.PUBLIC)
                .returns(long.class)
                .addAnnotation(Override.class)
                .addStatement(String.format("return %s.operationManager.<%s>count(%s.parseParent(this))", newClassName, className, newClassName))
                .build();

        // deleteAll method using builder
        MethodSpec deleteAllBuilderMethod = MethodSpec.methodBuilder("deleteAll")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addAnnotation(Override.class)
                .addParameter(ParameterizedTypeName.get(ClassName.get("com.k2future.westdao.core.wsql.builder", "JpqlBuilder"), parentClassName), "builder")
                .addStatement(String.format("return %s.operationManager.<%s>deleteAll(%s.parseParent(this), builder)", newClassName, className, newClassName))
                .build();
        // deleteAll method using builder
        MethodSpec updateBuilderMethod = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addAnnotation(Override.class)
                .addParameter(ParameterizedTypeName.get(ClassName.get("com.k2future.westdao.core.wsql.builder", "JpqlBuilder"), parentClassName), "builder")
                .addStatement(String.format("return %s.operationManager.<%s>update(%s.parseParent(this), builder)", newClassName, className, newClassName))
                .build();
        // findAll method using builder
        MethodSpec findAllBuilderMethod = MethodSpec.methodBuilder("findAll")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get("java.util", "List"), parentClassName))
                .addAnnotation(Override.class)
                .addParameter(ParameterizedTypeName.get(ClassName.get("com.k2future.westdao.core.wsql.builder", "JpqlBuilder"), parentClassName), "builder")
                .addStatement(String.format("return %s.operationManager.<%s>findAll(%s.parseParent(this), builder)", newClassName, className, newClassName))
                .build();

        // findOne method using builder
        MethodSpec findOneBuilderMethod = MethodSpec.methodBuilder("findOne")
                .addModifiers(Modifier.PUBLIC)
                .returns(parentClassName)
                .addAnnotation(Override.class)
                .addParameter(ParameterizedTypeName.get(ClassName.get("com.k2future.westdao.core.wsql.builder", "JpqlBuilder"), parentClassName), "builder")
                .addStatement(String.format("return %s.operationManager.<%s>findOne(%s.parseParent(this), builder)", newClassName, className, newClassName))
                .build();

        // page method using builder
        MethodSpec pageBuilderMethod = MethodSpec.methodBuilder("page")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get("org.springframework.data.domain", "Page"), parentClassName))
                .addAnnotation(Override.class)
                .addParameter(ClassName.get("org.springframework.data.domain", "Pageable"), "page")
                .addParameter(ParameterizedTypeName.get(ClassName.get("com.k2future.westdao.core.wsql.builder", "JpqlBuilder"), parentClassName), "builder")
                .addStatement(String.format("return %s.operationManager.<%s>page(%s.parseParent(this), page, builder)", newClassName, className, newClassName))
                .build();

        // count method using builder
        MethodSpec countBuilderMethod = MethodSpec.methodBuilder("count")
                .addModifiers(Modifier.PUBLIC)
                .returns(long.class)
                .addAnnotation(Override.class)
                .addParameter(ParameterizedTypeName.get(ClassName.get("com.k2future.westdao.core.wsql.builder", "JpqlBuilder"), parentClassName), "builder")
                .addStatement(String.format("return %s.operationManager.<%s>count(builder)", newClassName, className))
                .build();


        // add fields and methods to
        builder
                .addField(entityManager)
                .addMethod(setOperationManager)
                .addMethod(parseParentMethod)
                .addMethod(saveMethod)
                .addMethod(deleteByMethod)
                .addMethod(deleteAllMethod)
                .addMethod(findByIdMethod)
                .addMethod(updateByIdMethod)
                .addMethod(pageMethod)
                .addMethod(findAllMethod)
                .addMethod(findOneMethod)
                .addMethod(countMethod)
                .addMethod(findOneBuilderMethod)
                .addMethod(updateBuilderMethod)
                .addMethod(deleteAllBuilderMethod)
                .addMethod(findAllBuilderMethod)
                .addMethod(pageBuilderMethod)
                .addMethod(countBuilderMethod);

        // override parent setters
        overrideParentSetter(typeElement, childClassName, builder);

        TypeSpec generatedClass = builder.build();

        JavaFile javaFile = JavaFile.builder(
                        processingEnv.getElementUtils().getPackageOf(typeElement).toString(),
                        generatedClass)
                .build();

        JavaFileObject file = processingEnv.getFiler().createSourceFile(
                processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName() + "." + newClassName);
        try (Writer writer = file.openWriter()) {
            javaFile.writeTo(writer);
        }
    }

    /**
     * override parent setter
     */
    private void overrideParentSetter(TypeElement typeElement, ClassName childClassName, TypeSpec.Builder builder) {
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement instanceof VariableElement) {
                VariableElement field = (VariableElement) enclosedElement;
                String fieldName = field.getSimpleName().toString();
                String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                String setterMethodName = "set" + capitalizedFieldName;

                MethodSpec setterMethod = MethodSpec.methodBuilder(setterMethodName)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(childClassName)
                        .addParameter(TypeName.get(field.asType()), fieldName)
                        .addStatement("super." + setterMethodName + "(" + fieldName + ")")
                        .addStatement("return this")
                        .build();
                builder.addMethod(setterMethod);
            }
        }

    }
}