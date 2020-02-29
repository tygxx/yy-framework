package com.yy.framework.core.jpa;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;


/**    
 * 项目名称：webside-core    
 * 类名称：BasicIdEntity    
 * 类描述：主键自增长的实体基类
 * 创建人：codemonkey    
 * 创建时间：2015年4月30日 下午3:54:49    
 * 修改人：codemonkey    
 * 修改时间：2015年4月30日 下午3:54:49    
 * 修改备注：    
 * @version 1.0.0      
 */
@MappedSuperclass
public class BasicIdEntity implements Serializable{
	
	private static final long serialVersionUID = -490098889051162411L;
	
	public static final String TINYINT = "tinyint";
	
	/**主键Id*/
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	@ApiModelProperty(value="主键ID(修改时候必须输入)")
	private Long id;
	
	/**新增时间*/
	@Column(name = "insert_time", nullable = false, updatable = false)
	@ApiModelProperty(hidden=true)
	private Long insertTime = System.currentTimeMillis();
	
	/**最后一次修改的时间*/
	@Column(name = "last_update_time", nullable = false)
	@ApiModelProperty(hidden=true)
	private Long lastUpdateTime = System.currentTimeMillis();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Long insertTime) {
		this.insertTime = insertTime;
	}

	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}
