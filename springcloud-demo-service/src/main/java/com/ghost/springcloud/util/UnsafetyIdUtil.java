package com.ghost.springcloud.util;

import lombok.NoArgsConstructor;

/**
 * @program dubbo-demo
 * @description:
 * @author: jackchow
 * @create: 2021/11/28 13:44
 */
@NoArgsConstructor
public class UnsafetyIdUtil {

    public static int[] toCodePoints(CharSequence str) {
        if (str == null) {
            return null;
        } else if (str.length() == 0) {
            return new int[0];
        } else {
            String s = str.toString();
            int[] result = new int[s.codePointCount(0, s.length())];
            int index = 0;

            for (int i = 0; i < result.length; ++i) {
                result[i] = s.codePointAt(index);
                index += Character.charCount(result[i]);
            }

            return result;
        }
    }

    public static Long nextId() {
        return SnowFlakeUtil.getId();
    }

    public static String genUuid() {
        return String.valueOf(SnowFlakeUtil.getId());
    }

    public static Long getTimeFromId(Long id) {
        return (id >> 22) + 1480166465631L;
    }
}

