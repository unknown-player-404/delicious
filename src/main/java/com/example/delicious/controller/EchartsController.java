package com.example.delicious.controller;

import com.example.delicious.common.Result;
import com.example.delicious.entity.*;
import com.example.delicious.service.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/echarts")
public class EchartsController {
    @Resource
    private AdminInfoService adminInfoService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private ClassifyInfoService classifyInfoService;
    @Resource
    private CollectInfoService collectInfoService;
    @Resource
    private PraiseInfoService praiseInfoService;

    /**
     * 图表数据
     */
    @PostMapping("/get/{modelName}")
    public Result<List<Map<String, Object>>> getEchartsData(@PathVariable String modelName) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Integer> dataMap = new LinkedHashMap<>();
        String name;
        switch (modelName) {
            case "adminInfo" -> {
                name = "管理员";
                List<AdminInfo> list = adminInfoService.findAll();
                long male = list.stream().filter(items -> "男".equals(items.getSex())).count();
                long female = list.size() - male;
                dataMap.put("男", (int) male);
                dataMap.put("女", (int) female);
            }
            case "userInfo" -> {
                name = "用户";
                List<UserInfo> list = userInfoService.findAll();
                long male = list.stream().filter(item -> "男".equals(item.getSex())).count();
                long female = list.size() - male;
                dataMap.put("男", (int) male);
                dataMap.put("女", (int) female);
            }
            case "classifyInfo" -> {
                name = "菜谱分类";
                List<ClassifyInfo> list = classifyInfoService.findAll();
                for (ClassifyInfo info : list) {
                    dataMap.merge(info.getName(), 1, Integer::sum);
                }
            }
            case "collectInfo" -> {
                name = "收藏";
                List<CollectInfo> list = collectInfoService.findAll();
                for (CollectInfo info : list) {
                    dataMap.merge(info.getName(), 1, Integer::sum);
                }
            }
            case "praiseInfo" -> {
                name = "点赞";
                List<PraiseInfo> list = praiseInfoService.findAll();
                for (PraiseInfo info : list) {
                    dataMap.merge(info.getName(), 1, Integer::sum);
                }
            }
            default -> {
                return Result.success(resultList);
            }
        }
        resultList.add(buildPieData(name, dataMap));
        resultList.add(buildBarData(name, dataMap));
        return Result.success(resultList);
    }

    /**
     * 选项
     */
    @GetMapping("/options")
    public Result<List<Map<String, String>>> getOptions() {
        List<Map<String, String>> list = new ArrayList<>();
        String[][] items = {
                {"adminInfo", "管理员信息"},
                {"userInfo", "用户信息"},
                {"classifyInfo", "菜谱分类信息"},
                {"collectInfo", "收藏信息"},
                {"praiseInfo", "点赞信息"}
        };
        for (String[] item : items) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("value", item[0]);
            map.put("label", item[1]);
            list.add(map);
        }
        return Result.success(list);
    }

    /**
     * 饼图
     */

    public Map<String, Object> buildPieData(String name, Map<String, Integer> dataMap) {
        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("type", "pie");
        Map<String, String> title = new HashMap<>();
        title.put("text", name + "信息");
        chart.put("title", title);
        List<Map<String, Object>> seriesData = new ArrayList<>();
        //转换dataMap
        for (Map.Entry<String, Integer> e : dataMap.entrySet()) {
            Map<String, Object> newMap = new HashMap<>();
            newMap.put("name", e.getKey());
            newMap.put("value", e.getValue());
            seriesData.add(newMap);
        }
        chart.put("seriesData", seriesData);
        chart.put("legend", new ArrayList<>(dataMap.keySet()));
        return chart;
    }

    /**
     * 柱状图
     */
    public Map<String, Object> buildBarData(String name, Map<String, Integer> dataMap) {
        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("type", "bar");
        Map<String, String> title = new HashMap<>();
        title.put("text", name + "信息");
        chart.put("title", title);
        chart.put("xAxisData", new ArrayList<>(dataMap.keySet()));
        chart.put("seriesData", new ArrayList<>(dataMap.values()));
        chart.put("seriesName", name);
        return chart;
    }

}

