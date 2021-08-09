package me.tianzun.mirai.plugin;


import com.amihaiemil.eoyaml.*;

import java.io.*;
import java.util.*;

public class FileClass {
    private static Map<String, List<String>> data = new HashMap<>();

    public static Map<String, List<String>> getData() {
        return data;
    }

    public static void setData(Map<String, List<String>> data) {
        FileClass.data = data;
    }

    private static final File file = new File(System.getProperty("user.dir")+"\\data\\tz\\data.yaml");

    public static void load(){

        File folder=new File(System.getProperty("user.dir")+"\\data\\tz");
        if(!folder.exists()){
            folder.mkdir();
        }

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            YamlMapping yamlMapping = Yaml.createYamlInput(file).readYamlMapping();
            if(yamlMapping.keys() == null){
                return;
            }
            //遍历qq
            for(YamlNode yn : yamlMapping.keys()){
                String qq = refine(yn.toString());
                //根据qq获取qq下的内容（人品值和日期）
                YamlSequence ys = yamlMapping.yamlSequence(yn);
                String liststr = refine(ys.values().toString());
                String[] list = liststr.split(",");
                data.put(qq,
                        Arrays.asList(refine(list[0]),refine(list[1]))
                );
            }
            JavaPluginMain.getMain().getLogger().info("--data---");
            JavaPluginMain.getMain().getLogger().info(System.getProperty("line.separator")+data.toString());
            JavaPluginMain.getMain().getLogger().info("------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(){

        YamlMappingBuilder yamlMappingBuilder = Yaml.createYamlMappingBuilder();
        for(String qq : data.keySet()){
            List<String> list = data.get(qq);
            yamlMappingBuilder = yamlMappingBuilder.add(
                    qq,
                    Yaml.createYamlSequenceBuilder()
                            .add(list.get(0))
                            .add(list.get(1))
                            .build()
            );
        }
        try {
            FileWriter fw = new FileWriter(file,false);
            fw.write(yamlMappingBuilder.build().toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String refine(String s){
                //判断是否为*头尾引号*
        return s.startsWith("\"") && s.endsWith("\"")
                ?
                s.replace("---"+System.getProperty("line.separator"),"")
                        .replace(System.getProperty("line.separator")+"...","")
                        .replace("[","")
                        .replace("]","")
                        .replace(" ","")

                        //截取第二个字符到末尾（去除了开头引号）
                        .substring(1)
                        //截取开头到倒数第二个字符（去除了末尾引号）
                        .substring(0,s.length() -1)

                :
                s.replace("---"+System.getProperty("line.separator"),"")
                        .replace(System.getProperty("line.separator")+"...","")
                        .replace("[","")
                        .replace("]","")
                        .replace(" ","");
    }

    public static void setrp(String qq,List<String> list){
        data.put(qq,list);
    }
}
