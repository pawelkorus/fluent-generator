package fluentgenerator.processor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import java.util.Arrays;

import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class AnnotationDrivenInterfaceGeneratorTest {

	@Test
	public void testBasicBean() {
		Truth.assertAbout(javaSources())
			.that(Arrays.asList(
				JavaFileObjects.forResource("Basic.java")))
			.processedWith(new AnnotationDrivenInterfaceGenerator())
			.compilesWithoutError()
			.and()
			.generatesSources(JavaFileObjects.forResource("BasicGenerator.java"));
	}

}
