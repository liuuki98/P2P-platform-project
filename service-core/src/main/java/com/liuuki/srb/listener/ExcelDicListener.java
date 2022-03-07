package com.liuuki.srb.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.liuuki.srb.dao.DictMapper;
import com.liuuki.srb.entity.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelDicListener extends AnalysisEventListener<ExcelDictDTO> {

    private DictMapper dictMapper;

    private List<ExcelDictDTO> list=new ArrayList<>();



    private static final int LIST_SIZE=5;//每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收

    //传入mapper对象
    public ExcelDicListener(DictMapper dictMapper){
        this.dictMapper=dictMapper;
    }

    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {
        log.info("解析到一条记录："+excelDictDTO);
        list.add(excelDictDTO);
        if(list.size()>=LIST_SIZE){
            saveDate(list);
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if(list.size()!=0){
            saveDate(list);
        }
        log.info("所有数据解析完成");
    }

    public void saveDate(List<ExcelDictDTO> list){
        log.info("{}条数据，开始存储数据库！", list.size());
        dictMapper.saveExcel(list);
        log.info("存储数据库成功！");
    }
}
