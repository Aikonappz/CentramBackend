package com.centram.common.dto;

public class ExperienceFormatter {

    public static String format(int months){

        int years=months/12;

        int rem=months%12;

        if(years>0 && rem>0)
            return years+" years "+rem+" months";

        if(years>0)
            return years+" years";

        return rem+" months";
    }
}
