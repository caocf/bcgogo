package com.bcgogo.user;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-7-2
 * Time: 下午2:20
 */
public class ImpactVideoBlockFilter implements FileFilter {

        @Override
        public boolean accept(File path) {
            String filename = path.getName().toLowerCase();
            if(!filename.contains(".jpg")){
                return true;
            }else{
                return false;
            }
        }
    }
