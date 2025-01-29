package com.solvd;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /**
         * A function called pairs() takes an array, for example [3,1,8].
         * It should print all pairs, e.g. for mentioned example: [[3,1],[3,8],[1,8]]. 
         **/

        List<Integer> numbers = new ArrayList<>();
        numbers.add(3);
        numbers.add(1);
        numbers.add(8);

        List<List<Integer>> result = pairs(numbers);

        System.out.println(result);
    }

    public static List<List<Integer>> pairs(List<Integer> numbers) {

        List<List<Integer>> response = new ArrayList<>();

        for (int i = 0; i < numbers.size(); i++) {

            for (int j = i + 1; j < numbers.size(); j++) {

                List<Integer> temp = new ArrayList<>();
                temp.add(numbers.get(i));
                temp.add(numbers.get(j));

                response.add(temp);
            }

        }

        return response;

    }
}