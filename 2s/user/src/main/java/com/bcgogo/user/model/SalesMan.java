package com.bcgogo.user.model;

import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.enums.Sex;
import com.bcgogo.enums.user.Status;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-16
 * Time: 下午2:50
 */
@Entity
@Table(name = "sales_man")
public class SalesMan extends LongIdentifier {
  private static final Logger LOG = LoggerFactory.getLogger(SalesMan.class);
  public SalesMan(){

  }
  private String salesManCode;
  private Long agentId;
  private String name;
  private String mobile;
  private SalesManStatus status;
  private String address;
  private Long shopId;
  private Sex sex;
  @Deprecated
	private String department;
  private String position;
  private String identityCard;
  private Double salary;
  private Double allowance;
  private Long careerDate;//入职日期
  private String memo;
  private Long departmentId; //在没有分配账户之前使用
  private Long occupationId;
  private Long userGroupId;//职位暂时使用用户组id
  private String qq;
  private String email;

  @Column(name = "qq")
  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  @Column(name = "email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "memo")
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

  @Column(name = "sex")
  @Enumerated(EnumType.STRING)
	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

  @Column(name = "department")
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

  @Column(name = "position")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

  @Column(name = "identity_card")
	public String getIdentityCard() {
		return identityCard;
	}

	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

  @Column(name = "salary")
	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

  @Column(name = "allowance")
	public Double getAllowance() {
		return allowance;
	}

	public void setAllowance(Double allowance) {
		this.allowance = allowance;
	}

  @Column(name = "career_date")
	public Long getCareerDate() {
		return careerDate;
	}

	public void setCareerDate(Long careerDate) {
		this.careerDate = careerDate;
	}

  @Column(name = "shop_id")
	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

  @Column(name = "sales_man_code")
  public String getSalesManCode() {
    return salesManCode;
  }

  public void setSalesManCode(String salesManCode) {
    this.salesManCode = salesManCode;
  }
   @Column(name = "agent_id")
  public Long getAgentId() {
    return agentId;
  }

  public void setAgentId(Long agentId) {
    this.agentId = agentId;
  }
   @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public SalesManStatus getStatus() {
    return status;
  }

  public void setStatus(SalesManStatus status) {
    this.status = status;
  }
   @Column(name = "address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
  @Column(name = "department_id")
  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  @Column(name = "occupation_id")
  public Long getOccupationId() {
    return occupationId;
  }

  public void setOccupationId(Long occupationId) {
    this.occupationId = occupationId;
  }

  @Column(name = "user_group_id")
  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  public SalesManDTO toDTO(){
	  SalesManDTO salesManDTO = new SalesManDTO();
	  salesManDTO.setId(this.getId());
	  salesManDTO.setShopId(this.getShopId());
	  salesManDTO.setSalesManCode(this.getSalesManCode());
	  salesManDTO.setAgentId(this.getAgentId());
	  salesManDTO.setName(this.getName());
	  salesManDTO.setMobile(this.getMobile());
	  salesManDTO.setStatus(this.getStatus());
	  salesManDTO.setAddress(this.getAddress());
    if(this.getSex() != null) {
      salesManDTO.setSex(this.getSex().getName());
    }
	  salesManDTO.setDepartment(this.getDepartment());
	  salesManDTO.setPosition(this.getPosition());
	  salesManDTO.setIdentityCard(this.getIdentityCard());
	  salesManDTO.setSalary(this.getSalary());
	  salesManDTO.setAllowance(this.getAllowance());
	  salesManDTO.setCareerDate(this.getCareerDate());
	  String careerDateStr = DateUtil.dateLongToStr(this.getCareerDate(), DateUtil.DATE_STRING_FORMAT_DAY);
	  salesManDTO.setCareerDateStr(careerDateStr);
	  salesManDTO.setQq(this.getQq());
	  salesManDTO.setEmail(this.getEmail());
	  salesManDTO.setMemo(this.getMemo());
    salesManDTO.setDepartmentId(this.getDepartmentId());
    salesManDTO.setUserGroupId(this.getUserGroupId());
    salesManDTO.setOccupationId(this.getOccupationId());
	  return salesManDTO;
  }

  public SalesMan fromDTO(SalesManDTO salesManDTO,boolean setId){
	  if(setId) {
		  setId(salesManDTO.getId());
	  }
	  this.setShopId(salesManDTO.getShopId());
	  this.setSalesManCode(salesManDTO.getSalesManCode());
	  this.setAgentId(salesManDTO.getAgentId());
	  this.setName(salesManDTO.getName());
	  this.setMobile(salesManDTO.getMobile() );
	  this.setAddress(salesManDTO.getAddress());
    if(salesManDTO.getSex() != null) {
      this.setSex(salesManDTO.getSex());
    }
		if(salesManDTO.getStatus() != null) {
      this.setStatus(salesManDTO.getStatus());
    }
    this.setEmail(salesManDTO.getEmail());
    this.setQq(salesManDTO.getQq());
	  this.setIdentityCard(salesManDTO.getIdentityCard());
	  this.setSalary(salesManDTO.getSalary());
	  this.setAllowance(salesManDTO.getAllowance());
    if (salesManDTO.getCareerDate() != null) {
      this.setCareerDate(salesManDTO.getCareerDate());
    } else {
      try {
        this.setCareerDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, salesManDTO.getCareerDateStr()));
      } catch (ParseException e) {
        LOG.warn("data ParseException error.",e.getMessage());
      }
    }
    this.setMemo(salesManDTO.getMemo());
    this.setOccupationId(salesManDTO.getOccupationId());
    this.setUserGroupId(salesManDTO.getUserGroupId());
    this.setDepartmentId(salesManDTO.getDepartmentId());
	  return this;
  }

  public UserDTO toUserDTO() {
    UserDTO userDTO = new UserDTO();
    userDTO.setName(this.getName());
    userDTO.setEmail(this.getEmail());
    userDTO.setDepartmentId(this.getDepartmentId());
    if(this.getUserGroupId() != null) {
      userDTO.setUserGroupId(this.getUserGroupId());
    }

    userDTO.setMobile(this.getMobile());
    userDTO.setMemo(this.getMemo());
    userDTO.setStatusEnum(Status.active);
    userDTO.setQq(this.getQq());
    userDTO.setShopId(this.getShopId());
    userDTO.setSalesManId(this.getId());
    return userDTO;
  }
}
