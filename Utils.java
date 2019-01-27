package bus;

import bus.entities.Passenger;
import bus.entities.Stop;

public class Utils {

	public static String rtrim(String s) {
		int i = s.length() - 1;
		while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
			i--;
		}
		return s.substring(0, i + 1);
	}

	public static String reverse(String str) {
		return new StringBuilder(str).reverse().toString();
	}

	public static Stop[] reverse(Stop[] arr) {
		Stop[] out = new Stop[arr.length];

		for (int i = arr.length - 1, j = 0; i > 0; i--, j++) {
			out[j] = arr[i];
		}

		return out;
	}

	public static int addPassenger(Passenger[] passengers, int passengerCount, Passenger p) {
		passengers[passengerCount++] = p;
		return passengerCount;
	}

	public static int removePassenger(Passenger[] passengers, int passengerCount, Passenger p) {
		int i;
		for (i = 0; i < passengerCount; i++) {
			if (passengers[i] == null)
				continue;

			if (passengers[i].equals(p)) {
				break;
			}
		}

		passengers[i] = null;
		passengerCount--;

		// shift the array
		for (i = 0; i < passengers.length - 1; i++) {
			if (passengers[i] != null)
				continue;

			int last;
			for (last = i + 1; last < passengers.length; last++)
				if (passengers[last]==null)
					break;

			last--;

			try {
				System.arraycopy(passengers, i + 1, passengers, i, last - i);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(i + 1);
				System.out.println(i);
				System.out.println(last);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			if (last < passengers.length) {
				for (; last < passengers.length; last++)
					passengers[last] = null;
			}
		}

		return passengerCount;
	}

}
