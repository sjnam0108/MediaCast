package net.doohad.models;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import net.doohad.utils.Util;

public class DataSourceRequest {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceRequest.class);

    private int page;
    private int pageSize;
    private int take;
    private int skip;
    private List<SortDescriptor> sort;
    private List<GroupDescriptor> group;
    private List<AggregateDescriptor> aggregate;
    private List<ColumnDescriptor> column;
    private HashMap<String, Object> data;
    
    private Integer adminSiteId;
    private Integer adminStbId;
    private Integer adminCondId;
    private String adminType;
    
    private String reqStrValue1;
    private String reqStrValue2;
    private String reqStrValue3;
    
    private Integer reqIntValue1;
    private Integer reqIntValue2;
    
    private FilterDescriptor filter;
    
    public DataSourceRequest() {
        filter = new FilterDescriptor();
        data = new HashMap<String, Object>();
    }
    
    public HashMap<String, Object> getData() {
        return data;
    }
    
    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        data.put(key, value);
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTake() {
        return take;
    }

    public void setTake(int take) {
        this.take = take;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public Integer getAdminSiteId() {
		return adminSiteId;
	}

	public void setAdminSiteId(Integer adminSiteId) {
		this.adminSiteId = adminSiteId;
	}

    public Integer getAdminStbId() {
		return adminStbId;
	}

	public void setAdminStbId(Integer adminStbId) {
		this.adminStbId = adminStbId;
	}

	public Integer getAdminCondId() {
		return adminCondId;
	}

	public void setAdminCondId(Integer adminCondId) {
		this.adminCondId = adminCondId;
	}

	public String getAdminType() {
		return adminType;
	}

	public void setAdminType(String adminType) {
		this.adminType = adminType;
	}
	
	public String getReqStrValue1() {
		return reqStrValue1;
	}

	public void setReqStrValue1(String reqStrValue1) {
		this.reqStrValue1 = reqStrValue1;
	}

	public String getReqStrValue2() {
		return reqStrValue2;
	}

	public void setReqStrValue2(String reqStrValue2) {
		this.reqStrValue2 = reqStrValue2;
	}

	public String getReqStrValue3() {
		return reqStrValue3;
	}

	public void setReqStrValue3(String reqStrValue3) {
		this.reqStrValue3 = reqStrValue3;
	}

	public Integer getReqIntValue1() {
		return reqIntValue1;
	}

	public void setReqIntValue1(Integer reqIntValue1) {
		this.reqIntValue1 = reqIntValue1;
	}

	public Integer getReqIntValue2() {
		return reqIntValue2;
	}

	public void setReqIntValue2(Integer reqIntValue2) {
		this.reqIntValue2 = reqIntValue2;
	}

	public List<SortDescriptor> getSort() {
        return sort;
    }

    public void setSort(List<SortDescriptor> sort) {
        this.sort = sort;
    }

    public FilterDescriptor getFilter() {
        return filter;
    }

    public void setFilter(FilterDescriptor filter) {
        this.filter = filter;
    }

    public List<ColumnDescriptor> getColumn() {
        return column;
    }

    public void setColumn(List<ColumnDescriptor> column) {
        this.column = column;
    }

    private static void restrict(Junction junction, FilterDescriptor filter, Class<?> clazz) {
        String operator = filter.getOperator();
        String field = filter.getField();
        Object value = filter.getValue();
        boolean ignoreCase = filter.isIgnoreCase();

        String[] nullables = {"isnull", "isnotnull", "isempty", "isnotempty"};
        
        if (!Arrays.asList(nullables).contains(operator)) {
            try {
                Class<?> type = new PropertyDescriptor(field, clazz).getPropertyType();
                if (type == double.class || type == Double.class) {
                    value = Double.parseDouble(value.toString());
                } else if (type == float.class || type == Float.class) {
                    value = Float.parseFloat(value.toString());
                } else if (type == long.class || type == Long.class) {
                    value = Long.parseLong(value.toString());
                } else if (type == int.class || type == Integer.class) {
                    value = Integer.parseInt(value.toString());
                } else if (type == short.class || type == Short.class) {
                    value = Short.parseShort(value.toString());
                } else if (type == boolean.class || type == Boolean.class) {
                    value = Boolean.parseBoolean(value.toString());
                } else if (type == Date.class){
                    //SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
                    //String input = value.toString();
                    //value = df.parse(input);
                    value = Util.parseZuluTime(value.toString());
                }
            }catch (IntrospectionException e) {
            }catch(NumberFormatException nfe) {
            //}catch (ParseException e) {
            }
        }
        
        switch(operator) {
            case "eq":
                if (value instanceof String) {
                    junction.add(Restrictions.ilike(field, value.toString(), MatchMode.EXACT));
                } else {
                    junction.add(Restrictions.eq(field, value));
                }
                break;
            case "neq":
                if (value instanceof String) {
                    junction.add(Restrictions.not(Restrictions.ilike(field, value.toString(), MatchMode.EXACT)));
                } else {
                    junction.add(Restrictions.ne(field, value));
                }
                break;
            case "gt":
                junction.add(Restrictions.gt(field, value));
                break;
            case "gte":
                junction.add(Restrictions.ge(field, value));
                break;
            case "lt":
                junction.add(Restrictions.lt(field, value));
                break;
            case "lte":
                junction.add(Restrictions.le(field, value));
                break;
            case "startswith":
                junction.add(getLikeExpression(field, value.toString(), MatchMode.START, ignoreCase));
                break;
            case "endswith":
                junction.add(getLikeExpression(field, value.toString(), MatchMode.END, ignoreCase));
                break;
            case "contains":
                junction.add(getLikeExpression(field, value.toString(), MatchMode.ANYWHERE, ignoreCase));
                break;                
            case "doesnotcontain":
                junction.add(Restrictions.not(Restrictions.ilike(field, value.toString(), MatchMode.ANYWHERE)));
                break;
            case "isnull":
                junction.add(Restrictions.isNull(field));
                break;
            case "isnotnull":
                junction.add(Restrictions.isNotNull(field));
                break;
            case "isempty":
                junction.add(Restrictions.eq(field, ""));
                break;
            case "isnotempty":
                junction.add(Restrictions.not(Restrictions.eq(field, "")));                
                break;
        }

    }
    
    private static Criterion getLikeExpression(String field, String value, MatchMode mode, boolean ignoreCase) {
        SimpleExpression expression = Restrictions.like(field, value, mode);
        
        if (ignoreCase == true) {
            expression = expression.ignoreCase();
        }
        
        return expression;
    }
    
    private static void filter(Criteria criteria, FilterDescriptor filter, Class<?> clazz) {
        if (filter != null) {
            List<FilterDescriptor> filters = filter.filters;
            
            if (!filters.isEmpty()) {
                Junction junction = Restrictions.conjunction();
                
                if (!filter.getFilters().isEmpty()  && filter.getLogic().toString().equals("or")) {
                    junction = Restrictions.disjunction();
                }
                
                for(FilterDescriptor entry : filters) {
                    if (!entry.getFilters().isEmpty()) {
                        filter(criteria, entry, clazz);
                    } else {
                        restrict(junction, entry, clazz);
                    }
                }
                
                criteria.add(junction);
            }
        }
    }
    
    private static void sort(Criteria criteria, List<SortDescriptor> sort) {
        if (sort != null && !sort.isEmpty()) {
            for (SortDescriptor entry : sort) {
                String field = entry.getField();
                String dir = entry.getDir();
                
                if (dir.equals("asc")) {
                    criteria.addOrder(Order.asc(field).ignoreCase());    
                } else if (dir.equals("desc")) {
                    criteria.addOrder(Order.desc(field).ignoreCase());
                }
            }
        }
    }   
    
    @SuppressWarnings("rawtypes")
	private List<?> groupBy(List<?> items, List<GroupDescriptor> group, Class<?> clazz, HashMap<String, Class<?>> classMap, final Session session, 
    		List<SimpleExpression> parentRestrictions)  throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	ArrayList<Map<String, Object>> result = new ArrayList<Map<String,  Object>>();    	
    	                        
        if (!items.isEmpty() && group != null && !group.isEmpty()) {            
            List<List<SimpleExpression>> restrictions = new ArrayList<List<SimpleExpression>>();            
                    
            GroupDescriptor descriptor = group.get(0);
            //List<AggregateDescriptor> aggregates = descriptor.getAggregates();
            List<AggregateDescriptor> aggregates = getAggregate();
                    
        	final String field = descriptor.getField();
        	
        	String groupKey = "";
        	Class<?> groupClass = null;
        	Object groupValue = null;
        	Method accessor = null;
        	Method subAccessor = null;
        	if (field.indexOf(".") > -1) {
                Iterator it = classMap.entrySet().iterator();
                while (it.hasNext()) {
                	Map.Entry pairs = (Map.Entry)it.next();
                	groupKey = (String)pairs.getKey();
                	groupClass = (Class<?>)pairs.getValue();

                	if (field.startsWith(groupKey + ".")) {
                		accessor = new PropertyDescriptor(field.substring(0, field.indexOf(".")), clazz).getReadMethod();
                		subAccessor = new PropertyDescriptor(field.substring(groupKey.length() + 1), groupClass).getReadMethod();
                		break;
                	}
                }
        	} else {
        		accessor = new PropertyDescriptor(field, clazz).getReadMethod();
        	}
            
        	groupValue = accessor.invoke(items.get(0));
        	if (subAccessor != null) {
        		groupValue = subAccessor.invoke(groupValue);
        	}
                        
            List<Object> groupItems = createGroupItem(group.size() > 1, clazz, classMap, session, result, aggregates, field, groupValue, parentRestrictions);            
            
            List<SimpleExpression> groupRestriction = new ArrayList<SimpleExpression>(parentRestrictions);
            groupRestriction.add(Restrictions.eq(field, groupValue));
            restrictions.add(groupRestriction);
            
            for (Object item : items) {            	
            	Object currentValue = accessor.invoke(item);
            	if (subAccessor != null) {
            		currentValue = subAccessor.invoke(currentValue);
            	}
            	
				if (groupValue != null && !groupValue.equals(currentValue)) {
					groupValue = currentValue;
					groupItems = createGroupItem(group.size() > 1, clazz, classMap, session, result, aggregates, field, groupValue, parentRestrictions);
					
					groupRestriction = new ArrayList<SimpleExpression>(parentRestrictions);
		            groupRestriction.add(Restrictions.eq(field, groupValue));
		            restrictions.add(groupRestriction);
				}
				groupItems.add(item);
			}        
            
            if (group.size() > 1) {   
                Integer counter = 0;
            	for (Map<String,Object> g : result) {
            		g.put("items", groupBy((List<?>)g.get("items"), group.subList(1, group.size()), clazz, classMap, session, restrictions.get(counter++)));
				}
            }
        }
        
    	return result;
    }

    @SuppressWarnings("rawtypes")
	private List<Object> createGroupItem(Boolean hasSubgroups, Class<?> clazz, HashMap<String, Class<?>> classMap, final Session session, ArrayList<Map<String, Object>> result,
            List<AggregateDescriptor> aggregates,
            final String field, 
            Object groupValue,
            List<SimpleExpression> aggregateRestrictions) {
        
        Map<String, Object> groupItem = new HashMap<String, Object>();
        List<Object> groupItems = new ArrayList<Object>();
        
        result.add(groupItem);        
        
        if (groupValue instanceof Date) { // format date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = formatter.format(((Date)groupValue).getTime());
            groupItem.put("value", formattedDate);
        } else {
            groupItem.put("value", groupValue);
        }
        
        groupItem.put("field", field);
        groupItem.put("hasSubgroups", hasSubgroups);
         
        if (aggregates != null && !aggregates.isEmpty()) {           
            Criteria criteria = session.createCriteria(clazz);
            
            Iterator it = classMap.entrySet().iterator();
            while (it.hasNext()) {
            	Map.Entry pairs = (Map.Entry)it.next();

            	String key = (String)pairs.getKey();
            	criteria.createAlias(key, key);
            }
            
            filter(criteria, getFilter(), clazz); // filter the set by the selected criteria            
            
            SimpleExpression currentRestriction = Restrictions.eq(field, groupValue);
            
            if (aggregateRestrictions != null && !aggregateRestrictions.isEmpty()) {
                for (SimpleExpression simpleExpression : aggregateRestrictions) {                    
                    criteria.add(simpleExpression);
                }
            }
            criteria.add(currentRestriction);
            
            groupItem.put("aggregates", calculateAggregates(criteria, aggregates));
        } else {
            groupItem.put("aggregates", new HashMap<String, Object>());
        }
        groupItem.put("items", groupItems);
        return groupItems;
    }

    @SuppressWarnings({ "serial", "unchecked" })
    private static Map<String, Object> calculateAggregates(Criteria criteria, List<AggregateDescriptor> aggregates) {
        return (Map<String, Object>)criteria                    
                .setProjection(createAggregatesProjection(aggregates))                    
                .setResultTransformer(new ResultTransformer() {                                    
                    @Override
                    public Object transformTuple(Object[] value, String[] aliases) {                            
                        Map<String, Object> result = new HashMap<String, Object>();
                        
                        for (int i = 0; i < aliases.length; i++) {                                
                            String alias = aliases[i];
                            Map<String, Object> aggregate;
                            
                            String name = alias.split("_")[0];
                            if (result.containsKey(name)) {
                                ((Map<String, Object>)result.get(name)).put(alias.split("_")[1], value[i]);
                            } else {
                                aggregate = new HashMap<String, Object>();                                    
                                aggregate.put(alias.split("_")[1], value[i]);        
                                result.put(name, aggregate);
                            }
                        } 
                        
                        return result;
                    }
                    
                    @SuppressWarnings("rawtypes")
                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                })
                .list()
                .get(0);
    }    
    
    private static ProjectionList createAggregatesProjection(List<AggregateDescriptor> aggregates) {
        ProjectionList projections = Projections.projectionList();
        for (AggregateDescriptor aggregate : aggregates) {
            String alias = aggregate.getField() + "_" + aggregate.getAggregate();
            if (aggregate.getAggregate().equals("count")) {                
                projections.add(Projections.count(aggregate.getField()), alias);                
            } else if (aggregate.getAggregate().equals("sum")) {
                projections.add(Projections.sum(aggregate.getField()), alias);                
            } else if (aggregate.getAggregate().equals("average")) {
                projections.add(Projections.avg(aggregate.getField()), alias);                
            } else if (aggregate.getAggregate().equals("min")) {
                projections.add(Projections.min(aggregate.getField()), alias);                
            } else if (aggregate.getAggregate().equals("max")) {
                projections.add(Projections.max(aggregate.getField()), alias);                
            }
        }
        return projections;
    }
    
    private List<?>  group(final Criteria criteria, final Session session, final Class<?> clazz, 
    		HashMap<String, Class<?>> classMap) throws Exception {
    	List<?> result = new ArrayList<Object>();
    	List<GroupDescriptor> group = getGroup();
    	
        if (group != null && !group.isEmpty()) {
            try {
				result = groupBy(criteria.list(), group, clazz, classMap, session, new ArrayList<SimpleExpression>());
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | HibernateException
					| IntrospectionException e) {
				// Auto-generated catch block
				logger.error("group", e);
				
				throw e;
			}
        }
        return result;
    }

    private static long total(Criteria criteria) {
        long total = (long)criteria.setProjection(Projections.rowCount()).uniqueResult();
        
        criteria.setProjection(null);
        criteria.setResultTransformer(Criteria.ROOT_ENTITY);
        
        return total;
    }
    
    private static void page(Criteria criteria, int take, int skip) {
    	
		// hibernate 4.x -> 5.x로 올리면서 발생한 문제 해결
    	if (take == 0) {
            criteria.setMaxResults(10000);
    	} else {
            criteria.setMaxResults(take);
    	}

        criteria.setFirstResult(skip);
    }
    
    @SuppressWarnings("serial")
	public DataSourceResult toDataSourceResult(Session session, Class<?> clazz) {
    	return toDataSourceResult(session, clazz, new HashMap<String, Class<?>>() {});
    }    
    
    @SuppressWarnings("rawtypes")
	public DataSourceResult toDataSourceResult(Session session, Class<?> clazz, 
			HashMap<String, Class<?>> classMap) {
    	try {
            Criteria criteria = session.createCriteria(clazz);
            
            Iterator it = classMap.entrySet().iterator();
            while (it.hasNext()) {
            	Map.Entry pairs = (Map.Entry)it.next();

            	String key = (String)pairs.getKey();
            	criteria.createAlias(key, key);
            }
            
            filter(criteria, getFilter(), clazz);
            
            long total = total(criteria);       
                    
            sort(criteria, sortDescriptors());
        
            page(criteria, getTake(), getSkip());        
            
            DataSourceResult result = new DataSourceResult();
            
            result.setTotal(total);
            
            List<GroupDescriptor> groups = getGroup();
            
            if (groups != null && !groups.isEmpty()) {
            	result.setData(group(criteria, session, clazz, classMap));
            } else {
            	result.setData(criteria.list());	
            }
            
            List<AggregateDescriptor> aggregates = getAggregate();
            if (aggregates != null && !aggregates.isEmpty()) {
                result.setAggregates(aggregate(aggregates, getFilter(), session, clazz, classMap));
            }
            
            return result;
    	} catch (Exception e) {
    		logger.error("toDataSourceResult", e);
    		throw new RuntimeException();
    	}
    }
    
    public DataSourceResult toDataSourceResult(Session session, Class<?> clazz, 
			HashMap<String, Class<?>> classMap, String idFieldName, List<Integer> ids) {
    	return toDataSourceResult(session, clazz, classMap, idFieldName, ids, 0);
    }
    
    @SuppressWarnings("rawtypes")
	public DataSourceResult toDataSourceResult(Session session, Class<?> clazz, 
			HashMap<String, Class<?>> classMap, String idFieldName, List<Integer> ids, int lastMins) {
    	try {
            Criteria criteria = session.createCriteria(clazz);
            
            Iterator it = classMap.entrySet().iterator();
            while (it.hasNext()) {
            	Map.Entry pairs = (Map.Entry)it.next();

            	String key = (String)pairs.getKey();
            	criteria.createAlias(key, key);
            }
            
            filter(criteria, getFilter(), clazz);
            
            if (ids.size() == 0) {
            	ids.add(Integer.MAX_VALUE);
            }
            
        	criteria.add(Restrictions.in(idFieldName, ids));
        	
        	if (lastMins > 0) {
        		Calendar cal = Calendar.getInstance();
        		cal.setTime(new Date());
        		cal.add(Calendar.MINUTE, -1 * lastMins);
        		
        		criteria.add(Restrictions.ge("whoCreationDate", cal.getTime()));
        	}
        	

        	long total = total(criteria);       
                    
            sort(criteria, sortDescriptors());
        
            page(criteria, getTake(), getSkip());        
            
            DataSourceResult result = new DataSourceResult();
            
            result.setTotal(total);
            
            List<GroupDescriptor> groups = getGroup();
            
            if (groups != null && !groups.isEmpty()) {
            	result.setData(group(criteria, session, clazz, classMap));
            } else {
            	result.setData(criteria.list());	
            }
            
            List<AggregateDescriptor> aggregates = getAggregate();
            if (aggregates != null && !aggregates.isEmpty()) {
                result.setAggregates(aggregate(aggregates, getFilter(), session, clazz, classMap));
            }
            
            return result;
    	} catch (Exception e) {
    		throw new RuntimeException();
    	}
    }    
    
    @SuppressWarnings("rawtypes")
	public DataSourceResult toDataSourceResult(Session session, Class<?> clazz, 
			HashMap<String, Class<?>> classMap, Criterion criterion) {
    	try {
            Criteria criteria = session.createCriteria(clazz);
            
            Iterator it = classMap.entrySet().iterator();
            while (it.hasNext()) {
            	Map.Entry pairs = (Map.Entry)it.next();

            	String key = (String)pairs.getKey();
            	criteria.createAlias(key, key);
            }
            
            filter(criteria, getFilter(), clazz);
            
        	if (criterion != null) {
        		criteria.add(criterion);
        	}
        	

        	long total = total(criteria);       
                    
            sort(criteria, sortDescriptors());
        
            page(criteria, getTake(), getSkip());        
            
            DataSourceResult result = new DataSourceResult();
            
            result.setTotal(total);
            
            List<GroupDescriptor> groups = getGroup();
            
            if (groups != null && !groups.isEmpty()) {
            	result.setData(group(criteria, session, clazz, classMap));
            } else {
            	result.setData(criteria.list());	
            }
            
            List<AggregateDescriptor> aggregates = getAggregate();
            if (aggregates != null && !aggregates.isEmpty()) {
                result.setAggregates(aggregate(aggregates, getFilter(), session, clazz, classMap));
            }
            
            return result;
    	} catch (Exception e) {
    		logger.error("toDataSourceResult", e);
    		throw new RuntimeException();
    	}
    }    
    
    @SuppressWarnings("rawtypes")
	public DataSourceResult toIDDataSourceResult(Session session, Class<?> clazz, 
			HashMap<String, Class<?>> classMap, String idFieldName, List<String> IDs) {
    	try {
            Criteria criteria = session.createCriteria(clazz);
            
            Iterator it = classMap.entrySet().iterator();
            while (it.hasNext()) {
            	Map.Entry pairs = (Map.Entry)it.next();

            	String key = (String)pairs.getKey();
            	criteria.createAlias(key, key);
            }
            
            filter(criteria, getFilter(), clazz);
            
            if (IDs.size() == 0) {
            	IDs.add("AbCdEfG0123456789");
            }
            
        	criteria.add(Restrictions.in(idFieldName, IDs));
        	

        	long total = total(criteria);       
                    
            sort(criteria, sortDescriptors());
        
            page(criteria, getTake(), getSkip());        
            
            DataSourceResult result = new DataSourceResult();
            
            result.setTotal(total);
            
            List<GroupDescriptor> groups = getGroup();
            
            if (groups != null && !groups.isEmpty()) {
            	result.setData(group(criteria, session, clazz, classMap));
            } else {
            	result.setData(criteria.list());	
            }
            
            List<AggregateDescriptor> aggregates = getAggregate();
            if (aggregates != null && !aggregates.isEmpty()) {
                result.setAggregates(aggregate(aggregates, getFilter(), session, clazz, classMap));
            }
            
            return result;
    	} catch (Exception e) {
    		throw new RuntimeException();
    	}
    }
    
    @SuppressWarnings("rawtypes")
	public DataSourceResult toDataSourceResult(Session session, Class<?> clazz, 
			HashMap<String, Class<?>> classMap, HashMap<String, Class<?>> outerClassMap, 
			String idFieldName, List<Integer> ids) {
    	try {
            Criteria criteria = session.createCriteria(clazz);
            
            Iterator it = classMap.entrySet().iterator();
            while (it.hasNext()) {
            	Map.Entry pairs = (Map.Entry)it.next();

            	String key = (String)pairs.getKey();
            	criteria.createAlias(key, key);
            }
            
            it = outerClassMap.entrySet().iterator();
            while (it.hasNext()) {
            	Map.Entry pairs = (Map.Entry)it.next();

            	String key = (String)pairs.getKey();
            	criteria.createAlias(key, key, JoinType.LEFT_OUTER_JOIN);
            }
            
            filter(criteria, getFilter(), clazz);
            
            if (ids.size() == 0) {
            	ids.add(Integer.MAX_VALUE);
            }
            
        	criteria.add(Restrictions.in(idFieldName, ids));

        	long total = total(criteria);       
                    
            sort(criteria, sortDescriptors());
        
            page(criteria, getTake(), getSkip());        
            
            DataSourceResult result = new DataSourceResult();
            
            result.setTotal(total);
            
            List<GroupDescriptor> groups = getGroup();
            
            if (groups != null && !groups.isEmpty()) {
            	result.setData(group(criteria, session, clazz, classMap));
            } else {
            	result.setData(criteria.list());	
            }
            
            List<AggregateDescriptor> aggregates = getAggregate();
            if (aggregates != null && !aggregates.isEmpty()) {
                result.setAggregates(aggregate(aggregates, getFilter(), session, clazz, classMap));
            }
            
            return result;
    	} catch (Exception e) {
    		throw new RuntimeException();
    	}
    }    
    
    @SuppressWarnings("rawtypes")
	private static Map<String, Object> aggregate(List<AggregateDescriptor> aggregates, FilterDescriptor filters, Session session, Class<?> clazz,
    		HashMap<String, Class<?>> classMap) {
        Criteria criteria = session.createCriteria(clazz);
        
        Iterator it = classMap.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry pairs = (Map.Entry)it.next();

        	String key = (String)pairs.getKey();
        	criteria.createAlias(key, key);
        }
        
        filter(criteria, filters, clazz);
        
        return calculateAggregates(criteria, aggregates);                
    }    
    
    private List<SortDescriptor> sortDescriptors() {
        List<SortDescriptor> sort = new ArrayList<SortDescriptor>();
        
        List<GroupDescriptor> groups = getGroup();
        List<SortDescriptor> sorts = getSort();
        
        if (groups != null) {
            sort.addAll(groups);
        }        
        
        if (sorts != null) {
            sort.addAll(sorts);
        }
        return sort;        
    }
    
    public List<GroupDescriptor> getGroup() {
        return group;
    }

    public void setGroup(List<GroupDescriptor> group) {
        this.group = group;
    }
    
    public List<AggregateDescriptor> getAggregate() {
        return aggregate;
    }

    public void setAggregate(List<AggregateDescriptor> aggregate) {
        this.aggregate = aggregate;
    }

    public static class SortDescriptor {
        private String field;
        private String dir;
        
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }
    
    public static class GroupDescriptor extends SortDescriptor {        
        private List<AggregateDescriptor> aggregates;

        public GroupDescriptor() {
            aggregates = new ArrayList<AggregateDescriptor>();
        }        
        
        public List<AggregateDescriptor> getAggregates() {
            return aggregates;
        }
    }
    
    public static class AggregateDescriptor {
        private String field;
        private String aggregate;
        
        public String getField() {
            return field;
        }
        public void setField(String field) {
            this.field = field;
        }
        
        public String getAggregate() {
            return aggregate;
        }
        
        public void setAggregate(String aggregate) {
            this.aggregate = aggregate;
        }
    }
    
    public static class FilterDescriptor {
        private String logic;
        private List<FilterDescriptor> filters;        
        private String field;
        private Object value;        
        private String operator;
        private boolean ignoreCase = true;
        
        public FilterDescriptor() {
            filters = new ArrayList<FilterDescriptor>();
        }
        
        public String getField() {
            return field;
        }
        public void setField(String field) {
            this.field = field;
        }
        public Object getValue() {
            return value;
        }
        public void setValue(Object value) {
            this.value = value;
        }
        public String getOperator() {
            return operator;
        }
        public void setOperator(String operator) {
            this.operator = operator;
        }
        
        public String getLogic() {
            return logic;
        }
        
        public void setLogic(String logic) {
            this.logic = logic;
        }

        public boolean isIgnoreCase() {
            return ignoreCase;
        }

        public void setIgnoreCase(boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
        }
        
        public List<FilterDescriptor> getFilters() {
            return filters;
        }
    }

    public static class ColumnDescriptor {
        private String field;
        private String title;
        
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
    
    @SuppressWarnings("rawtypes")
	public DataSourceResult toEffectiveDataSourceResult(Session session, Class<?> clazz, 
			HashMap<String, Class<?>> classMap, boolean isEffectiveMode) {
    	try {
            Criteria criteria = session.createCriteria(clazz);
            
            Iterator it = classMap.entrySet().iterator();
            while (it.hasNext()) {
            	Map.Entry pairs = (Map.Entry)it.next();

            	String key = (String)pairs.getKey();
            	criteria.createAlias(key, key);
            }
            
        	if (isEffectiveMode) {
    			Date time = new Date();
    			
    			Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
    			Criterion rest2 = Restrictions.isNull("effectiveEndDate");
    			Criterion rest3 = Restrictions.gt("effectiveEndDate", time);

        		criteria.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3)));
        	}
            
            filter(criteria, getFilter(), clazz);

        	long total = total(criteria);       
                    
            sort(criteria, sortDescriptors());
        
            page(criteria, getTake(), getSkip());        
            
            DataSourceResult result = new DataSourceResult();
            
            result.setTotal(total);
            
            List<GroupDescriptor> groups = getGroup();
            
            if (groups != null && !groups.isEmpty()) {
            	result.setData(group(criteria, session, clazz, classMap));
            } else {
            	result.setData(criteria.list());	
            }
            
            List<AggregateDescriptor> aggregates = getAggregate();
            if (aggregates != null && !aggregates.isEmpty()) {
                result.setAggregates(aggregate(aggregates, getFilter(), session, clazz, classMap));
            }
            
            return result;
    	} catch (Exception e) {
    		throw new RuntimeException();
    	}
    }
}
