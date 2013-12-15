package controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.avaje.ebean.Expression;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.avaje.ebean.Expr;

import models.Address;
import models.LogApi;
import models.LogLogin;
import play.mvc.Result;
import views.html.log.overview;

/**
 * Controller to display log entries.
 * 
 * @author dal
 * 
 */
public class Log extends Eshomo {

    private final static int PAGE_SIZE = 5;
    private final static int VISIBLE_PAGE = 50;
    

    public static Result getApiLogs(String filter, int page) throws InterruptedException,
            ExecutionException {
        if (isLoggedIn() && isAdminUser()) {

            ExpressionList<LogApi> query = LogApi.find.where();
            if (filter != null && !filter.isEmpty()){
                List<Address> uList = Address.find.where().or(Expr.ilike("firstname", "%" + filter + "%")
                        ,Expr.ilike("lastname", "%" + filter + "%")).findList();
                List<Integer> ids = new ArrayList<Integer>();
                for(Address a : uList)
                    ids.add(a.getUser().getId());          

                Expression  e1 = Expr.or(Expr.ilike("info", "%" + filter + "%"), 
                        Expr.ilike("params", "%" + filter + "%"));
                Expression  e2 = Expr.or(e1, 
                        Expr.in("user.id",ids));
                
                Expression exp = Expr.or(e1,e2);
                if("anonymous".contains(filter.toLowerCase()) || filter.toLowerCase().contains("anonymous")){
                    exp = Expr.or(exp,Expr.isNull("user"));
                }
                query = query.add(exp);
            
            }
            PagingList<LogApi> list = query.orderBy("createdAt desc")
                    .findPagingList(PAGE_SIZE);
            
            LogViewModel model = getLogViewModel(list, filter, page);
            model.title = "Api Log - Übersicht";
            model.typ = "api";
            
            model.linkModel = new PageLinkModel(VISIBLE_PAGE, model.currentPage, model.totalPage);

            return ok(overview.render(model));
        } else
            return forbidden();
    }

    public static Result getLoginLogs(String filter, int page) throws InterruptedException,
            ExecutionException {
        if (isLoggedIn() && isAdminUser()) {

            ExpressionList<LogLogin> query = LogLogin.find.where();
            if (filter != null && !filter.isEmpty()){
                List<Address> uList = Address.find.where().or(Expr.ilike("firstname", "%" + filter + "%")
                        ,Expr.ilike("lastname", "%" + filter + "%")).findList();
                List<Integer> ids = new ArrayList<Integer>();
                for(Address a : uList)
                    ids.add(a.getUser().getId());
                
                Expression exp = Expr.or(Expr.ilike("info", "%" + filter + "%"),Expr.in("user.id",ids));
                if("anonymous".contains(filter.toLowerCase()) || filter.toLowerCase().contains("anonymous")){
                    exp = Expr.or(exp,Expr.isNull("user"));
                }
                
                query = query.add(exp);
            }
            
            PagingList<LogLogin> list = query.orderBy("createdAt desc")
                    .findPagingList(PAGE_SIZE);
            
            LogViewModel model = getLogViewModel(list, filter, page);
            model.title = "Login Log - Übersicht";
            model.typ = "login";
            model.linkModel = new PageLinkModel(VISIBLE_PAGE, model.currentPage, model.totalPage);
            return ok(overview.render(model));
        } else
            return forbidden();
    }

    private static <T> LogViewModel getLogViewModel(PagingList<T> list, String filter, int page)
            throws InterruptedException, ExecutionException {
        LogViewModel model = new LogViewModel();
        model.filter = filter;
        model.currentPage = page < 0 ? 0 : page;
        model.entries = getLogViewEntries(list.getPage(model.currentPage).getList());
        model.totalPage = list.getTotalPageCount();

        return model;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> List<LogViewEntryModel> getLogViewEntries(List<T> list) {
        ArrayList<LogViewEntryModel> result = new ArrayList<LogViewEntryModel>();
        for (T item : list) {
            LogViewEntryModel model = new LogViewEntryModel();
            Class clazz = ((T) item).getClass();
            try {
                Method info = clazz.getMethod("getInfo");
                Method created = clazz.getMethod("getCreatedAt");
                Method uMethod = clazz.getMethod("getUser");
                model.info = (String) info.invoke(item);
                if (model.info == null)
                    model.info = "";
                Date d = (Date) created.invoke(item);
                model.date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(d);
                models.User user = (models.User) uMethod.invoke(item);
                if(user != null && user.getAddresses() != null && user.getAddresses().get(0) != null)
                    model.userName = user.getAddresses().get(0).getFirstname() + " " + user.getAddresses().get(0).getLastname();
                else
                    model.userName = "Anonymous";
            } catch (Exception e) {
                continue;
            }
            try {
                Method param = clazz.getMethod("getParams");
                model.params = (String) param.invoke(item);
                if (model.params == null)
                    model.params = "";
            } catch (Exception e) {
            }
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
    public static class LogViewEntryModel {
        public String date = "";
        public String userName = "";
        public String info = "";
        public String params = "";
    }

    /**
     * View model for log model.
     * 
     * @author dal
     * 
     */
    public static class LogViewModel {
        public List<LogViewEntryModel> entries = new ArrayList<LogViewEntryModel>();
        public int currentPage = 0;
        public int totalPage = 0;
        public String filter = "";
        public String typ = "";
        public String title = "";
        public PageLinkModel linkModel;
    }
    
    public static class PageLinkModel{
        private int maxNumber;
        private int current;
        private int pages;
        
        public PageLinkModel(int maxNumber, int current, int pages ){
            if(maxNumber < 1)
                throw new IllegalArgumentException("maxNumber");
            if(pages < 0)
                throw new IllegalArgumentException("pages");
            if(current < 0 || current > pages)
                current = 0;
            this.maxNumber = maxNumber;
            this.current = current;
            this.pages = pages;
        }
        
        public boolean hasPrev(){
            if(current > 0)
                return true;
            else
                return false;
        }
        
        public boolean hasNext(){
            if(current < pages - 1)
                return true;
            else
                return false;
        }
        
        public int getNextLink(){
            if(hasNext())
                return  (current + 1) ;     
            else
                return pages - 1 ; 
        }
        
        public int getPrevLink(){
            if(hasPrev())
                return  (current - 1);     
            else
                return 0 ; 
        }
        
        public int getLastLink(){
            return pages - 1;            
        }
        
        public boolean hasMore(){
            List<Integer> ids = getPageLinks();           
            if(ids.contains(pages - 1))
                return false;
            else
                return true;
        }
        
        public boolean hasLess(){
            List<Integer> ids = getPageLinks();           
            if(ids.contains(0))
                return false;
            else
                return true;
        }
               
        public List<Integer> getPageLinks(){
            ArrayList<Integer> list = new ArrayList<Integer>();
            int lb;
            int ub;
            int li = maxNumber / 2;
            int re = maxNumber % 2 == 0 ? li - 1 : li; 
            if(maxNumber >= pages){
                lb = 0;
                ub = pages - 1;
            } else {
                if(current - li < 0){
                    lb = 0;
                    ub = li + re;
                } else if ( current + re >= pages){
                    lb = pages - 1 - (li + re);
                    ub = pages - 1;
                }
                else {
                    lb = current - li;
                    ub = current + re;
                }
            }    
            for(int i = lb; i <= ub; i++){
                 list.add(i);
            }
            return list;
        }        

    }
    
}
