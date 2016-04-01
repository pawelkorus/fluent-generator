package fluentgenerator.supplier;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author pkorus
 */
public class RandomStringSupplier implements Supplier<String> {

	private final Supplier<Integer> _stringLength;
	private final IntFunction<String> _stringGenerator;
	private final String _prefix;
	private final String _suffix;
	
	private RandomStringSupplier(
		Supplier<Integer> stringLength,
		IntFunction<String> stringGenerator,
		String prefix,
		String suffix
	) {
		_stringLength = stringLength;
		_stringGenerator = stringGenerator;
		_prefix = prefix;
		_suffix = suffix;
	}
	
	@Override
	public String get() {
		int length = _stringLength.get();
		if(length == 0) return "";
		StringBuilder b = new StringBuilder();
		return b.append(_prefix).append(_stringGenerator.apply(length))
			.append(_suffix).toString();
	}

	public static class Builder 
	{
		private Supplier<Integer> _stringLength = () -> 10;
		private IntFunction<String> _stringGenerator = RandomStringUtils::randomAlphanumeric;
		private String _prefix = "";
		private String _suffix = "";
		
		public Builder() {}
		
		public Builder length(int length) {
			assert length >= 0 : "length should be >= 0, but " + length + " was passed";
			_stringLength = () -> length;
			return this;
		}

		public Builder randomLength(int min, int max) {
			assert min >= 0 : "min value should be >= 0, but " + min + " was passed";
			assert max >= 0 : "max value should be >= 0, but " + max + " was passed";
			assert min <= max : "min vlaue should be <= max";

			_stringLength = () -> {
				ThreadLocalRandom random = ThreadLocalRandom.current();
				return random.nextInt(min, max + 1);
			};

			return this;
		}

		public Builder alphanumeric() {
			_stringGenerator = RandomStringUtils::randomAlphanumeric;
			return this;
		}

		public Builder numeric() {
			_stringGenerator = RandomStringUtils::randomNumeric;
			return this;
		}

		public Builder alphabetic() {
			_stringGenerator = RandomStringUtils::randomAlphabetic;
			return this;
		}

		public Builder prefix(String prefix) {
			_prefix = prefix;
			return this;
		}

		public Builder suffix(String suffix) {
			_suffix = suffix;
			return this;
		}
		
		public RandomStringSupplier build() {
			RandomStringSupplier provider = new RandomStringSupplier(
				_stringLength, _stringGenerator, _prefix, _suffix
			);
			return provider;
		}
		
		public static Builder create() { return new Builder(); }
	}
}
