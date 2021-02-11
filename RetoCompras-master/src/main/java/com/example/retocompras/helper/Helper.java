package com.example.retocompras.helper;

public class Helper {

	public static int getHour(int hour) {
		switch (hour) {
		case 24:
			return 0;
		case 25:
			return 1;
		case 26:
			return 2;
		case 27:
			return 3;
		case 28:
			return 4;
		case 29:
			return 5;
		case 30:
			return 6;
		case 31:
			return 7;
		case 32:
			return 8;
		case 33:
			return 9;
		case 34:
			return 10;
		case 35:
			return 11;
		case 36:
			return 12;
		default:
			return hour;
		}
	}
}
