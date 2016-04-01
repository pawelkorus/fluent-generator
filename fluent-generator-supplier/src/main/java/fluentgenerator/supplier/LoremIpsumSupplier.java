package fluentgenerator.supplier;

import de.svenjacobs.loremipsum.LoremIpsum;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 *
 * @author pkorus
 */
public class LoremIpsumSupplier implements Supplier<String> {
	
	private final LoremIpsum _loremIpsum = new LoremIpsum();
	private Supplier<Integer> _numberOfWords;

	public LoremIpsumSupplier() {
		_numberOfWords = () -> 10;
	}

	public LoremIpsumSupplier numberOfWords(int numberOfWords) {
		_numberOfWords = () -> numberOfWords;
		return this;
	}
	
	public LoremIpsumSupplier randomNumberOfWords(int min, int max) {
		_numberOfWords = () -> {
			ThreadLocalRandom random = ThreadLocalRandom.current();
			return random.nextInt(min, max + 1);
		};
		return this;
	}
	
	@Override
	public String get() {
		return _loremIpsum.getWords(_numberOfWords.get());
	}
}
