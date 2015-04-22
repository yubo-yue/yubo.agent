package org.simple.agent.metric;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedClassTransformer implements ClassFileTransformer {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TimedClassTransformer.class);
	private ClassPool classPool;

	public TimedClassTransformer() {
		classPool = new ClassPool();
		classPool.appendSystemPath();

		try {
			classPool.appendPathList(System.getProperty("java.class.path"));
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] transform(ClassLoader loader, String fullyQualifiedClassName,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String className = fullyQualifiedClassName.replace('/', '.');
		classPool.appendClassPath(new ByteArrayClassPath(className,
				classfileBuffer));

		try {
			CtClass ctClass = classPool.get(className);
			if (ctClass.isFrozen()) {
				LOGGER.debug("Skip class {}: is frozen", className);
				return null;
			}

			if (ctClass.isPrimitive() || ctClass.isArray()
					|| ctClass.isAnnotation() || ctClass.isEnum() || ctClass.isInterface()) {
				LOGGER.debug("Skip class {}: not a class", className);
				return null;
			}
			
			boolean isClassModified = false;
			for (CtMethod method : ctClass.getDeclaredMethods()) {
				//if method is annotated, add the code to measure the time
				if (method.hasAnnotation(Measured.class)) {
					if (method.getMethodInfo().getCodeAttribute() == null) {
						LOGGER.debug("Skip method {}", method.getLongName());
						continue;
					}
					
					LOGGER.debug("Instrumenting method {}", method.getLongName());
					method.addLocalVariable("__metricStartTime", CtClass.longType);
					method.insertBefore("__metricStartTime = System.currentTimeMillis();");
					String metricName = ctClass.getName() + "." + method.getName();
					method.insertAfter("org.simple.agent.metric.MetricReporter.reportTime(\"" + metricName + "\", System.currentTimeMillis() - __metricStartTime);");
					isClassModified = true;
				}
			}
			
			if (!isClassModified) {
				return null;
			}
			
			return ctClass.toBytecode();
		} catch (Exception e) {
			LOGGER.debug("Skip class {} : {}", className, e.getMessage());
			return null;
		} 
	}

}
