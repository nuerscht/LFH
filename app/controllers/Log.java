package controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.avaje.ebean.Expr;

import models.LogApi;
import play.mvc.Result;

public class Log extends Eshomo {
    
    private final static int PAGE_SIZE = 5;
    
    public static Result logs(String filter, int page){
        if(isLoggedIn() && isAdminUser()){

            return getLogs(filter,page);            
        }
        else
            return forbidden();
    }
    
    private static Result getApiLogs(String filter, int page)  {

        List<LogViewEntryModel> entries = new ArrayList<LogViewEntryModel>();
        
        ExpressionList<LogApi> query = LogApi.find.where();
        if(filter != null || !filter.isEmpty())
            query = query
            .or(Expr.ilike("info", "%" + filter + "%"),
                    Expr.ilike("params", "%" + filter + "%") );
        PagingList<LogApi> list = query.findPagingList(PAGE_SIZE);    
        
        return ok(overview.render(getLogViewModel(list, filter, page)));
    }
    
    private static <T> LogViewModel getLogViewModel(PagingList<T> list,String filter, int page) throws InterruptedException, ExecutionException{
        LogViewModel model = new LogViewModel();
        Future<Integer> rowCount = list.getFutureRowCount();
        model.filter = filter;
        model.currentPage = page < 0 ? 0 : page;
        model.entries = getLogViewEntries(list.getPage(model.currentPage).getList());
        model.typ = "api";
        model.totalPage = rowCount.get();   
        
        return model;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> List<LogViewEntryModel> getLogViewEntries(List<T> list){
        ArrayList<LogViewEntryModel> result = new ArrayList<LogViewEntryModel>();
        for (T item : list) {
            LogViewEntryModel model = new LogViewEntryModel();
            Class clazz = ((T) item).getClass();            
             try {
                Method info = clazz.getMethod("getInfo", (Class) null);
                Method created = clazz.getMethod("getCreatedAt", (Class) null);
                model.info = (String) info.invoke(item, (Object) null);
                if(model.info == null)
                    model.info = "";
                Date d = (Date) created.invoke(item, (Object) null);
                model.date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(d);                
            } catch (Exception e) {
                continue;
            } 
             try {
                 Method param = clazz.getMethod("getParams", (Class) null);
                 model.params = (String) param.invoke(item, (Object) null);
                 if(model.params == null)
                     model.params = "";
            } catch (Exception e) {}
             result.add(model);             
        }
        return result;
    }

    /**
     * View model for log entry. 
     * 
     * @author dal
     *
     */
    public static class LogViewEntryModel{
        public String date;
        public String info;
        public String params;
    }
    
    /**
     * View model for log model. 
     * 
     * @author dal
     *
     */
    public static class LogViewModel{
        public List<LogViewEntryModel> entries;
        public int currentPage;
        public int totalPage;
        public String filter;
        public String typ;
    }    
}
