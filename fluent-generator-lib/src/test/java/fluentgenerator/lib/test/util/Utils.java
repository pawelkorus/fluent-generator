package fluentgenerator.lib.test.util;

/**
 *
 * @author pkorus
 */
public abstract class Utils {
	public static class Repeater {
		private final Runnable _runnable;
		
		public Repeater(Runnable r) {
			_runnable = r;
		}
		
		public void times(int number) {
			for(int i = number; i > 0; i--) {
				_runnable.run();
			}
		}
	}
	
	public static Repeater repeat(Runnable r) {
		return new Repeater(r);
	}
}
