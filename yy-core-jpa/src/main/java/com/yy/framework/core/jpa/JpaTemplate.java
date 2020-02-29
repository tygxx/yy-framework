package com.yy.framework.core.jpa;

import com.eversec.framework.core.annotation.DateFormat;
import com.eversec.framework.core.data.QueryCondition;
import com.eversec.framework.core.data.QueryResult;
import com.eversec.framework.core.exception.BusinessException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.internal.SessionImpl;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * 类名称: JpaTemplate<br>
 * 类描述: 基于java jpa封装的jpa操作模版<br>
 * 修改时间: 2016年11月28日下午6:08:48<br>
 * @author mateng@eversec.cn
 */
public class JpaTemplate {
	
	private static Logger logger = Logger.getLogger(JpaTemplate.class.getName());
	
	protected EntityManager entityManager;

	public JpaTemplate(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * 根据主键查询单个数据<br>
	 * 例如：User user = jpaTemplate.findOne(User.class, id);
	 * @param entityClass 类型
	 * @param primaryKey	主键
	 * @return 查询对象
	 */
	public <T> T findOne(Class<T> entityClass, Object primaryKey) {
		return this.entityManager.find(entityClass, primaryKey);
	}
	
	/**
	 * 根据sql语句查询单个对象<br>
	 * 常用于根据unique字段查询，根据主键查询<br>
	 * 例如：<br>
	 * String hql = "from User u where u.loginName = ?0";<br>
	 * User user = jpaTemplate.findOne(hql, loginName);
	 * @param hql	hql语句
	 * @param primaryKeys	主键（动态数组）
	 * @return 查询对象
	 */
	public <T> T findOne(String hql, Object... primaryKeys) {
		Query query = entityManager.createQuery(hql);
		if(primaryKeys != null) {
			for (int i = 0; i < primaryKeys.length; i++) {
				query.setParameter(i, primaryKeys[i]);
			}
		}
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	/**
	 * 查询所有数据<br>
	 * 例如：<br>
	 * List userList = jpaTemplate.findOne(User.class);
	 * @param clazz 类型
	 * @return	集合
	 */
	public <E> List<E> findAll(Class<E> clazz) {
        CriteriaQuery<E> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(clazz);
        criteriaQuery.from(clazz);
        return getEntityManager().createQuery(criteriaQuery).getResultList();
    } 
	
	/**
	 * 查询总记录数<br>
	 * 例如：<br>
	 * long total = jpaTemplate.findTotal(User.class);
	 * @param clazz 要查询对象的class
	 * @return 总记录数
	 */
	public Long findTotal(Class clazz) {
		String name = getEntityName(clazz);
		String sql = "select count(1) from " + name;
		Query query = getEntityManager().createQuery(sql);
		return (Long) query.getSingleResult();
    } 

	/**
	 * 删除元素，调用前最好通过findOne得到对象再删除
	 * @param entity 要删除的对象
	 */
	public void delete(Object entity) {
		getEntityManager().remove(entity);
	}
	
	/**
	 * 根据主键删除<br>
	 * 例如：jpaTemplate.delete(User.class, id);
	 * @param entityClass 要删除对象的class
	 * @param primaryKey 主键
	 * @return 被删除的对象
	 */
	public <T> T delete(Class<T> entityClass, Object primaryKey) {
		T t = findOne(entityClass, primaryKey);
		if(t != null) {
			this.delete(t);
		}
		return t;
	}

	/**
	 * 保存对象（新增、修改）
	 * @param entity 要保存的对象
	 * @return 保存后的对象
	 */
	public <T> T save(T entity) {
		return this.getEntityManager().merge(entity);
	}
	
	/**
	 * 复合分页查询<br>
	 * 例如：<br>
	 * String hql = "from User u where u.name like ?0 and u.age between ?1 and ?2";<br>
	 * List params = new ArrayList();<br>
	 * params.add("张%");//姓张的用户<br>
	 * params.add(20);//年龄大于25的用户<br>
	 * params.add(25);//年龄小于25的用户<br>
	 * QueryResult result = jpaTemplate.getQueryResult(User.class, hql, params, page);<br>
	 * @param entityClass 类的class
	 * @param hql	hql语句
	 * @param page	分页条件(包含起始查询位置和pageSize，为空表示不分页)
	 * @param params	HQL中的参数，参数List的顺序要复合sql中的占位符顺序
	 * @return 	QueryResult：包括总记录书，记录集合
	 */
	public <T> QueryResult<T> getQueryResult(Class<T> entityClass, String hql, QueryCondition page, List<Object> params) {
		QueryResult<T> queryResult = new QueryResult<T>();
		Object[] args = null;
		if(!CollectionUtils.isEmpty(params)) {
			args = new Object[params.size()];
			params.toArray(args);
		}
		queryResult.setTotalRecord(getCount(hql, args));
		queryResult.setResultData(getList(entityClass, hql, page, args));
		return queryResult;
	}
	
	/**
	 * 根据条件查询总记录数<br>
	 * 例如：<br>
	 * String hql = "from User u where u.name like ?0 and u.age between ?1 and ?2";<br>
	 * hql语句也可以写成：String hql = "select count(u.id) from User u where u.name like ?0 and u.age between ?1 and ?2";<br>
	 * Object[] params = new Object[3];<br>
	 * params[0] = "张%"; //姓张的用户<br>
	 * params[1] = 20;	  //年龄大于25的用户<br>
	 * params[2] = 25;	  //年龄小于25的用户<br>
	 * long total = jpaTemplate.getCount(hql, params);<br>
	 * @param hql	hql语句
	 * @param params	HQL中的参数（类型Object...），数组的顺序要符合sql中的占位符顺序
	 * @return 总记录数
	 */
	public long getCount(String hql, Object... params) {
		String sql = hql.trim().replaceAll("FROM", "from").replaceAll("FETCH", "").replace("fetch", "");
		if(sql.startsWith("from")) {
			sql = "select count(1) " + sql;
		}
		
		Query totalQuery = getEntityManager().createQuery(sql);
		
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				totalQuery.setParameter(i, params[i]);
			}
		}
		
		return (Long) totalQuery.getSingleResult();
	}
	
	/**
	 * 根据条件查询记录集合<br>
	 * 例如：<br>
	 * String hql = "from User u where u.name like ?0 and u.age between ?1 and ?2";<br>
	 * Object[] params = new Object[3];<br>
	 * params[0] = "张%"; //姓张的用户<br>
	 * params[1] = 20;	  //年龄大于25的用户<br>
	 * params[2] = 25;	  //年龄小于25的用户<br>
	 * List userList = jpaTemplate.getList(User.class, hql, page, params);<br>
	 * @param entityClass 类的class
	 * @param hql	hql语句
	 * @param page	分页条件(包含起始查询位置和pageSize，为空表示不分页)
	 * @param params	HQL中的参数（类型Object...），数组的顺序要符合sql中的占位符顺序
	 * @return list
	 */
	public <T> List<T> getList(Class<T> entityClass, String hql, QueryCondition page, Object... params) {
		Query dataQuery = getEntityManager().createQuery(hql);
		
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				dataQuery.setParameter(i, params[i]);
			}
		}
		
		if(page != null && page.getMaxResult().intValue() != 0) {
			dataQuery.setFirstResult(page.getStartPosition());
			dataQuery.setMaxResults(page.getMaxResult());
		}
		
		return dataQuery.getResultList();
	}
	
	/**
	 * 执行hql，适用于写delete , update语句<br>
	 * 例如：<br>
	 * 1、jpaTemplate.execute("update User u set u.age = u.age + 1 where u.id = ?0", id);<br>
	 * 2、jpaTemplate.execute("delete from User u where u.id = ?0", id)
	 * @param hql HQL语句
	 * @param params HQL中的参数(可变长参数)，参数数组的顺序要复合HQL中的占位符顺序
	 * @return
	 */
	public int execute(String hql, Object... params) {
		Query query = entityManager.createQuery(hql);
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}
		return query.executeUpdate();
	}
	
	/**
	 * 执行纯SQL，适用于写delete , update , insert语句<br>
	 * 例如：<br>
	 * 1、jpaTemplate.execute("update table_user u set u.age = ?0 where u.id = ?1", age, id);<br>
	 * 2、jpaTemplate.execute("delete from table_user u where u.id = ?0", id)
	 * @param sql SQL语句
	 * @param params SQL中的参数(可变长参数)，参数数组的顺序要复合sql中的占位符顺序
	 * @return
	 */
	public int executeNativeSql(String sql, Object... params) {
		Query query = entityManager.createNativeQuery(sql);
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}
		return query.executeUpdate();
	}
	
	/**
	 * 执行纯SQL，适用于写select语句，sql语句返回是多行多列（多行一列）的数据<br>
	 * 例如：jpaTemplate.getListByNativeSql("select * from table_user u set u.age = ?0 where u.name = ?1", age, name);<br>
	 * @param sql SQL语句
	 * @param params SQL中的参数(可变长参数)，参数数组的顺序要复合sql中的占位符顺序
	 * @return List，list中装的是Object[]
	 */
	public List getListByNativeSql(String sql, Object... params) {
		Query query = entityManager.createNativeQuery(sql);
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}
		return query.getResultList();
	}
	
	/**
	 * 执行纯SQL，适用于写select语句，sql语句返回是多行多列（多行一列）的数据<br>
	 * @param sql	sql语句
	 * @param page	分页参数
	 * @param params	SQL中的参数(可变长参数)，参数数组的顺序要复合sql中的占位符顺序
	 * @return List，list中装的是Object[]
	 */
	public List getListByNativeSql(String sql, QueryCondition page, Object... params) {
		Query query = entityManager.createNativeQuery(sql);
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}
		
		if(page != null && page.getMaxResult().intValue() != 0) {
			query.setFirstResult(page.getStartPosition());
			query.setMaxResults(page.getMaxResult());
		}
		
		return query.getResultList();
	}
	
	
	/**
	 * 执行纯SQL，适用于写select语句，sql语句返回一行多列（或者一行一列的数据）<br>
	 * @param sql 执行的sql语句
	 * @param params SQL中的参数(可变长参数)，参数数组的顺序要复合sql中的占位符顺序
	 * @return Object 返回值
	 */
	public Object getOneByNativeSql(String sql, Object... params) {
		Query query = entityManager.createNativeQuery(sql);
		if(params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}
		return query.getSingleResult();
	}
	
	/**
	 * 批量插入（暂时只适用于MYSQL数据库，插入数据中有特殊字符的无法处理）<br>
	 * <code>该方法不推荐使用，建议使用insertBatch(String sql, List valuesList, int batchSize, BatchJpaStatementSetter batchJpaTemplateSetter)</code>
	 * @param clazz	表对应的实体类
	 * @param columns 插入数据库的列名
	 * @param fileds 反射list中对象的属性名
	 * @param valuesList 需要批量插入的list
	 * @param batchSize	每次批量入库的大小
	 * @return 成功插入的条数
	 */
	@Deprecated
	public int insertBatch(Class clazz, String[] columns, String[] fileds, List<?> valuesList, int batchSize) {
		String tableName = getTableName(clazz);
		int total = 0;
		int pageSize = valuesList.size() % batchSize == 0 ?  valuesList.size() / batchSize : (valuesList.size() / batchSize) + 1;
		for (int page = 0; page < pageSize; page++) {
			StringBuffer sql = new StringBuffer();
			sql.append("insert into " + StringUtils.lowerCase(tableName) + "(" + StringUtils.join(columns, ",") + ") values ");
			int start = page * batchSize;
			int end = (page + 1) * batchSize;
			if(end > valuesList.size()) {
				end = valuesList.size();
			}
			for (int i = start; i < end; i++) {
				String[] values = new String[fileds.length];
				Object object = valuesList.get(i);
				for (int j = 0; j < fileds.length; j++) {
					Object value = null;
					try {
						value = PropertyUtils.getProperty(object, fileds[j]);
					} catch (Exception e) {
						logger.warning("批量插入时，无法得到对象的属性值，" + e.getMessage());
						throw new BusinessException(BusinessException.REFLECTION_EXCEPTION, "批量插入时，无法得到对象的属性值", e);
					}
					if(value == null) {
						values[j] = "null";
					}else if(value instanceof String) {
						values[j] = "'" + value + "'";
					}else if(value instanceof Date) {
						Field field = null;
						try {
							field = clazz.getDeclaredField(fileds[j]);
						} catch (NoSuchFieldException | SecurityException e) {
							logger.warning("批量插入时，无法得到对象的属性值，" + e.getMessage());
							throw new BusinessException(BusinessException.REFLECTION_EXCEPTION, "批量插入时，无法得到对象的属性", e);
						}
						DateFormat dateFormat = field.getAnnotation(DateFormat.class);
						values[j] = "'" + DateFormatUtils.format((Date)value, dateFormat.value()) + "'";
					}else {
						values[j] = value.toString();
					}
				}
				sql.append("(" + StringUtils.join(values, ",") + ") ");
				if(i != end - 1) {
					sql.append(", ");
				}
			}
			total +=executeNativeSql(sql.toString());
		}
		return total;
	}
	
	/**
	 * 使用jdbc进行批量插入（可以兼容各种数据库，支持插入特殊字符）<br>
	 * 使用样例:
	 * <pre>
		List&lt;DemoDomain&gt; list = new ArrayList&lt;&gt;();//批量插入的模拟数据
		for (int i = 0; i &gt; 100; i++) {
			DemoDomain domain = new DemoDomain();
			domain.setXXX("name-" + i);//生成模拟数据
			list.add(domain);
		}
		String sql = "insert into ebp_demo(name, remark, insert_time, last_update_time) values(?,?,?,?)";
		jpaTemplate.insertBatch(sql, list, 10, new BatchJpaStatementSetter() {
			public void setValues(PreparedStatement ps, Object row, int i) throws SQLException {
				DemoDomain demoDomain = (DemoDomain) row;
				ps.setString(1, demoDomain.getName());
				ps.setString(2, demoDomain.getRemark());
				ps.setLong(3, demoDomain.getInsertTime());
				ps.setLong(4, demoDomain.getLastUpdateTime());
			}
		});
	 * </pre>
	 * @param sql 标准的插入sql语句
	 * @param valuesList 批量插入的集合
	 * @param batchSize 每次批量入库的缓冲区大小
	 * @param batchJpaTemplateSetter 批量插入的回调设置器
	 */
	public void insertBatch(String sql, List<?> valuesList, int batchSize, BatchJpaStatementSetter batchJpaTemplateSetter) {
		int pageNo = valuesList.size() % batchSize == 0 ?  valuesList.size() / batchSize : (valuesList.size() / batchSize) + 1;
		SessionImpl session = entityManager.unwrap(SessionImpl.class);
		Connection con = session.connection();
		PreparedStatement pst = null;
		try {
			pst = (PreparedStatement) con.prepareStatement(sql);
			for (int page = 0; page < pageNo; page++) {
				int start = page * batchSize;
				int end = (page + 1) * batchSize;
				if(end > valuesList.size()) {
					end = valuesList.size();
				}
				for (int i = start; i < end; i++) {
					batchJpaTemplateSetter.setValues(pst, valuesList.get(i), i);
					pst.addBatch();
				}
				// 执行批量更新  
				pst.executeBatch();
			}
		} catch (SQLException e) {
			logger.warning("执行批量插入sql失败，" + e.getMessage());
			throw new BusinessException(BusinessException.DB_CONNETION_EXCEPTION, "执行批量插入sql失败，" + e.getMessage(), e);
		} finally {
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
//			session.close();
//			try {
//				con.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	/**
	 * 执行ddl语句 <br>
	 * 例如：jpaTemplate.ddl("alter table sys_audit_login_log modify column logout_app_name varchar(64)");
	 * @param sql ddl sql语句
	 */
	public void ddl(String sql) {
		SessionImpl session = entityManager.unwrap(SessionImpl.class);
		Connection con = session.connection();
		Statement stmt = null;
		try {
			stmt = (Statement) con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			logger.warning("执行ddl语句失败，" + e.getMessage());
			throw new BusinessException(BusinessException.DB_CONNETION_EXCEPTION, "执行ddl语句失败，" + e.getMessage(), e);
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
//			session.close();
//			try {
//				con.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	protected String getTableName(Class entityClass) {
		String tableName = entityClass.getSimpleName();
		Table table = (Table) entityClass.getAnnotation(Table.class);
		if(table == null) {
			throw new BusinessException(BusinessException.ANNOTATION_EXCEPTION, "无法获取实体类的Table注解：" + entityClass);
		}
		if (StringUtils.isNotBlank(table.name())) {
			tableName = table.name();
		}
		return tableName;
	}
	
	protected String getEntityName(Class entityClass) {
		String entityname = entityClass.getSimpleName();
		Entity entity = (Entity) entityClass.getAnnotation(Entity.class);
		if(entity == null) {
			throw new BusinessException(BusinessException.ANNOTATION_EXCEPTION, "无法获取实体类的Entity注解：" + entityClass);
		}
		if (StringUtils.isNotBlank(entity.name())) {
			entityname = entity.name();
		}
		return entityname;
	}
	
	public static void main(String[] args) {
		JpaTemplate jpaTemplate = new JpaTemplate(null);
		//模拟数据
		List<BasicIdEntity> valuesList = new ArrayList<>();
		for (int i = 0; i < 100000; i++) {
			BasicIdEntity entity1 = new BasicIdEntity();
			entity1.setId(i + 0l);
			valuesList.add(entity1);
		}
		
		//插入数据库的列名
		String[] columns = new String[]{
				"id", "insert_time", "last_update_time", "date"
		};
		//反射list中对象的属性名
		String[] fileds = new String[]{
				"id", "insertTime", "lastUpdateTime", "date"
		};
		//批量插入
		jpaTemplate.insertBatch(BasicIdEntity.class, columns, fileds, valuesList, 1000);
		
//		try {
//			System.out.println("try");
//			throw new BusinessException(-1, "try抛出的异常");
//		} catch (Exception e) {
//			System.out.println("catch");
//			throw new BusinessException(-2, "cath 抛出的异常", e);
//		}finally {
//			System.out.println("finally");
//		}
	}
}
