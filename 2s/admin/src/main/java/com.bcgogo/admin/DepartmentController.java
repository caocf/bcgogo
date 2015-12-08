package com.bcgogo.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-7
 * Time: 下午8:08
 * 部门 controller
 */
@Controller
@RequestMapping("/department.do")
public class DepartmentController {
  private static final Logger LOG = LoggerFactory.getLogger(DepartmentController.class);

  @ResponseBody
  @RequestMapping(params = "method=getDepartments")
  public Object getDepartments(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    return "[\n" +
        "    {\n" +
        "        \"text\": \"统购\",\n" +
        "        \"leaf\": false,\n" +
        "        \"expanded\": false,\n" +
        "        \"children\": [\n" +
        "            {\n" +
        "                \"text\": \"分公司\",\n" +
        "                \"expanded\": false,\n" +
        "                \"children\": [\n" +
        "                    {\n" +
        "                        \"text\": \"部门1\",\n" +
        "                        \"leaf\": true\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"text\": \"部门2\",\n" +
        "                        \"leaf\": true\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"text\": \"分公司2\",\n" +
        "                \"leaf\": true\n" +
        "            }\n" +
        "        ]\n" +
        "    }\n" +
        "]" ;
//    DepartmentDTO departmentDTO;
//    List<DepartmentDTO> departmentDTOs = new ArrayList<DepartmentDTO>();
//    departmentDTO = new DepartmentDTO();
//    departmentDTO.setId(1l);
//    departmentDTO.setName("统购平台");
//    departmentDTO.setParentId(null);
//    departmentDTO.setLeaf(false);
//    departmentDTOs.add(departmentDTO);
//    departmentDTO = new DepartmentDTO();
//    departmentDTO.setId(2l);
//    departmentDTO.setName("总经理办公室");
//    departmentDTO.setLeaf(true);
//    departmentDTO.setParentId(1l);
//    departmentDTOs.add(departmentDTO);
//    departmentDTO = new DepartmentDTO();
//    departmentDTO.setId(3l);
//    departmentDTO.setName("销售部");
//    departmentDTO.setLeaf(false);
//    departmentDTO.setParentId(1l);
//    departmentDTOs.add(departmentDTO);
//    departmentDTO = new DepartmentDTO();
//    departmentDTO.setId(4l);
//    departmentDTO.setName("技术部");
//    departmentDTO.setLeaf(true);
//    departmentDTO.setParentId(3l);
//    departmentDTOs.add(departmentDTO);
//    departmentDTO = new DepartmentDTO();
//    departmentDTO.setId(5l);
//    departmentDTO.setName("销售部-昆山");
//    departmentDTO.setLeaf(true);
//    departmentDTO.setParentId(3l);
//    departmentDTOs.add(departmentDTO);
//    departmentDTO = new DepartmentDTO();
//    departmentDTO.setId(6l);
//    departmentDTO.setName("销售部-吴江");
//    departmentDTO.setLeaf(true);
//    departmentDTO.setParentId(3l);
//    departmentDTOs.add(departmentDTO);
//    return departmentDTOs;
  }
}
