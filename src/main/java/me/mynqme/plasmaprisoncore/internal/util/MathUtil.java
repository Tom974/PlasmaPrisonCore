package me.mynqme.plasmaprisoncore.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MathUtil {
    public static int max(int... nums) {
        List<Integer> numsList = new ArrayList<>();
        Arrays.stream(nums).forEach(numsList::add);
        Collections.sort(numsList);
        return numsList.get(numsList.size()-1);
    }
}
