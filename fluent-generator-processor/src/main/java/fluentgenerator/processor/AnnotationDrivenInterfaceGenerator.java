package fluentgenerator.processor;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import fluentgenerator.annotation.FluentGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@SupportedAnnotationTypes("fluentgenerator.annotation.FluentGenerator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationDrivenInterfaceGenerator extends AbstractProcessor {

	private static final Map<String, Class<?>> primitiveTypesToClasses = new HashMap<String, Class<?>>() {{
		put("byte", Byte.class);
		put("char", Character.class);
		put("short", Short.class);
		put("int", Integer.class);
		put("long", Long.class);
		put("float", Float.class);
		put("double", Double.class);
		put("boolean", Boolean.class);
		put("void", Void.class);
	}};

	public AnnotationDrivenInterfaceGenerator() {
		super();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		roundEnv.getElementsAnnotatedWith(FluentGenerator.class).stream()
			.filter(v -> v instanceof TypeElement)
			.map(v -> (TypeElement) v)
			.forEach(this::processTypeElement);

		return true;
	}

	private void processTypeElement(TypeElement element) {
		final PackageElement packageElement = (PackageElement) element.getEnclosingElement();

		Context context = new Context();
		context.packageName = packageElement.getQualifiedName().toString();
		context.generatorName = element.getSimpleName() + "Generator";
		context.modelName = element.getSimpleName().toString();

		element.getEnclosedElements().stream()
			.map(this::extractMethod)
			.filter(v -> v.isPresent())
			.map(v -> v.get())
			.forEach(context.methods::add);

		try {
			final JavaFileObject javaFile = processingEnv.getFiler()
				.createSourceFile(context.packageName + "." + context.generatorName);
			final Writer fileWriter = javaFile.openWriter();

			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile("GeneratorInterface.mustache");
			mustache.execute(fileWriter, context).flush();

			fileWriter.close();

		} catch (IOException e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
		}
	}

	private Optional<Method> extractMethod(Element element) {
		if(element instanceof ExecutableElement == false) return Optional.empty();
		if(element.getKind() != ElementKind.METHOD) return Optional.empty();
		ExecutableElement executableElement = (ExecutableElement) element;

		String setterPrefix = "set";

		String methodName = executableElement.getSimpleName().toString();
		if(methodName.length() <= setterPrefix.length()) return Optional.empty();
		if(methodName.substring(0, setterPrefix.length()).compareToIgnoreCase(setterPrefix) != 0) return Optional.empty();
		String generatorMethodName = methodName.substring(setterPrefix.length());
		generatorMethodName = generatorMethodName.substring(0, 1).toLowerCase() + generatorMethodName.substring(1);

		List<? extends VariableElement> parameters = executableElement.getParameters();
		if(parameters.size() != 1) return Optional.empty();

		String generatorArgumentType = parameters.get(0).asType().toString();
		generatorArgumentType = mapPrimitiveType(generatorArgumentType);

		Method m = new Method();
		m.methodName = generatorMethodName;
		m.argumentType = generatorArgumentType;
		return Optional.of(m);
	}

	private String mapPrimitiveType(String type) {
		return primitiveTypesToClasses.containsKey(type)? primitiveTypesToClasses.get(type).getSimpleName() : type;
	}

	private static class Context {
		String packageName = "";
		String generatorName = "";
		String modelName = "";
		Collection<Method> methods = new ArrayList<>();
	}

	private static class Method {
		String methodName = "";
		String argumentType = "";
	}
}
