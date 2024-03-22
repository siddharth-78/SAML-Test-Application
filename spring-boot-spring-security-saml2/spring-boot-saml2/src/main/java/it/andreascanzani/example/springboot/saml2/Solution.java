package it.andreascanzani.example.springboot.saml2;

import java.util.*;
import java.io.*;

public class Solution {

    static class Pair {

        long value;
        int index;

        public Pair(long value, int index) {
            this.value = value;
            this.index = index;
        }
    }

    // DO NOT REMOVE
    static Scanner in;
    static PrintWriter out;

    public static void main(String[] args) throws FileNotFoundException {
        // Conditional setup for input and output

            // Running on Codeforces or other online judges
            in = new Scanner(System.in);
            out = new PrintWriter(System.out);

        // Your code here
        int testcases = in.nextInt();
        while(testcases-- > 0) {
            int n = in.nextInt();

            int[] arr = new int[n];
            for(int i = 0; i < n; i++) {
                arr[i] = in.nextInt();
            }

            // solve(arr, n);

            if(dp(arr, 0, arr[0])) {
                out.println("YES");
            }
            else {
                out.println("NO");
            }
        }

        // Close both the Scanner and PrintWriter to release resources
        out.close();
    }

    private static void solve(int[] arr, int n) {

        int maxValue = -1;

        for(int i = 0; i < n - 1; i++) {

            if(arr[i] <= arr[i + 1]) {

                if(isSplitable(arr[i]) && isUsable(arr[i], maxValue)) {
                    String stringValue = String.valueOf(arr[i]);
                    maxValue = stringValue.charAt(stringValue.length() - 1) - '0';
                }
                else {
                    maxValue = arr[i];
                }
            }

            else {
                if(isSplitable(arr[i]) && isUsable(arr[i], maxValue)) {
                    String stringValue = String.valueOf(arr[i]);
                    maxValue = stringValue.charAt(stringValue.length() - 1) - '0';
                }
                else {
                    out.println("NO");
                    return;
                }
            }

        }

        if(arr[n - 1] >= maxValue)
            out.println("YES");
        else
            out.println("NO");
        return;
    }

    private static boolean isSplitable(int value) {

        char[] arr = String.valueOf(value).toCharArray();

        for(int i = 0; i < arr.length - 1; i++) {

            if(arr[i] > arr[i + 1]) return false;
        }

        return true;
    }

    private static boolean isUsable(int value, int maxValue) {

        String stringValue = String.valueOf(value);

        int firstValue = (stringValue.charAt(0) - '0');
        int lastValue = (stringValue.charAt(stringValue.length() - 1) - '0');

        if(firstValue >= maxValue && lastValue >= maxValue) {
            return true;
        }
        else{
            return false;
        }
    }

    private static boolean dp(int[] arr, int index, int maxValue) {

        if(index >= arr.length) {
            return true;
        }


        if(arr[index] >= maxValue) {

            // 2 options - split it or keep it
            if(isSplitable(arr[index]) &&  isUsable(arr[index], maxValue)) {

                String stringFormOfTheNum = String.valueOf(arr[index]);
                int lastDigit = stringFormOfTheNum.
                        charAt(stringFormOfTheNum.length() - 1) - '0';

                boolean splitIt = dp(arr, index + 1, lastDigit);
                boolean keepIt = dp(arr, index + 1, arr[index]);

                return splitIt || keepIt;
            }
            // 1 option - keep it
            else {
                boolean keepIt = dp(arr, index + 1, arr[index]);
                return keepIt;
            }
        }
        else {

            // 1 option - have to split it
            if(isSplitable(arr[index]) &&  isUsable(arr[index], maxValue)) {

                String stringFormOfTheNum = String.valueOf(arr[index]);
                int lastDigit = stringFormOfTheNum.
                        charAt(stringFormOfTheNum.length() - 1) - '0';

                boolean splitIt = dp(arr, index + 1, lastDigit);

                return splitIt;
            }
            else {
                return false;
            }
        }
    }
}






