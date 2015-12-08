package com.bcgogo.config.upyun;

import com.bcgogo.config.dto.upYun.UpYunFileDTO;
import com.bcgogo.config.dto.upYun.UpYunFilePolicyDTO;
import com.bcgogo.config.dto.upYun.UpYunPolicyDTO;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-12
 * Time: 下午6:12
 * To change this template use File | Settings | File Templates.
 */
public class UpYunManager {
  private static final Logger LOG = LoggerFactory.getLogger(UpYunManager.class);

  private UpYun upYunClient;
  private static UpYunManager instance;

  private UpYunManager() throws Exception {
    String bucket = ConfigUtils.getUpYunBucket();
    String username = ConfigUtils.getUpYunUsername();
    String password = ConfigUtils.getUpYunPassword();
    if (StringUtils.isBlank(bucket) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
      throw new Exception("upYun bucket、username、password can't null");
    }
    upYunClient = new UpYun(bucket, username, password);
  }

  public static synchronized UpYunManager getInstance() {
    if (instance == null) {
      try {
        instance = new UpYunManager();
      } catch (Exception e) {
        LOG.error("UpYunHelper init fail!");
        LOG.error(e.getMessage());
      }
    }
    return instance;
  }

  /**
   * 图片长传的原图处理  后台上传图片
   * @return
   */
  public Map<String, String> generateDefaultUpYunParams(){
    // 设置缩略图的参数
    Map<String, String> params = new HashMap<String, String>();
    params.put(UpYun.PARAMS.KEY_X_GMKERL_QUALITY.getValue(), "60");
    params.put(UpYun.PARAMS.KEY_X_GMKERL_UNSHARP.getValue(),"true");
    return params;
  }
  /**
   * 图片长传的原图处理  前台form上传图片
   * @return
   */
  public UpYunFileDTO generateDefaultUpYunFileDTO(Long shopId){
    UpYunFilePolicyDTO upYunFilePolicyDTO = new UpYunFilePolicyDTO(DateUtils.addMinutes(new Date(), ConfigUtils.getUpYunExpiration()));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.BUCKET, ConfigUtils.getUpYunBucket()));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.SAVE_KEY, String.format(ConfigUtils.getUpYunImagePath(),shopId)));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.ALLOW_FILE_TYPE, ConfigUtils.getUpYunAllowImageType()));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.CONTENT_LENGTH_RANGE, UpYunPolicyDTO.DEFAULT_CONTENT_LENGTH_RANGE));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.X_GMKERL_QUALITY, "60"));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.X_GMKERL_UNSHARP, "true"));
    return generateUpYunFileDTO(upYunFilePolicyDTO);
  }

  /**
   * 图片上传的原图处理  手机appUser上传图片
   * @return
   */
  public UpYunFileDTO generateAppUserUpYunFileDTO(Long appUserId){
    UpYunFilePolicyDTO upYunFilePolicyDTO = new UpYunFilePolicyDTO(DateUtils.addMinutes(new Date(), ConfigUtils.getUpYunExpiration()));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.BUCKET, ConfigUtils.getUpYunBucket()));
    String pathParam = "APP_"+appUserId;
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.SAVE_KEY, String.format(ConfigUtils.getUpYunImagePath(), pathParam)));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.ALLOW_FILE_TYPE, ConfigUtils.getUpYunAllowImageType()));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.CONTENT_LENGTH_RANGE, UpYunPolicyDTO.DEFAULT_CONTENT_LENGTH_RANGE));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.X_GMKERL_QUALITY, "60"));
    upYunFilePolicyDTO.addCondition(new UpYunPolicyDTO(UpYunPolicyDTO.X_GMKERL_UNSHARP, "true"));
    return generateUpYunFileDTO(upYunFilePolicyDTO);
  }



  /**
   * 图片长传的原图处理  前台form上传图片
   * @return
   */
  public UpYunFileDTO generateUpYunFileDTO(UpYunFilePolicyDTO upYunFilePolicyDTO) {
    try {
      String bucket = ConfigUtils.getUpYunBucket();
      String policy = new String(Base64.encodeBase64(upYunFilePolicyDTO.toJsonString().getBytes("UTF-8"), false), "UTF-8");
      String signature = UpYun.md5(policy+"&"+ConfigUtils.getUpYunSecretKey());
      return new UpYunFileDTO(bucket,signature, policy);
    } catch (Exception e) {
      LOG.error("generateUpYunFileDTO fail!");
      LOG.error(e.getMessage());
    }
    return null;
  }

  public boolean writeFile(String filePath, byte[] datas, boolean auto,Map<String, String> params) {
    upYunClient.setDebug(true);
    return upYunClient.writeFile(filePath,datas,auto,params);
  }

  public boolean writeFile(String filePath, String datas, boolean auto) {
    upYunClient.setDebug(true);
    return upYunClient.writeFile(filePath,datas,auto);
  }
  public boolean writeFile(String filePath,File file, boolean auto) throws Exception{
    upYunClient.setDebug(true);
    return upYunClient.writeFile(filePath,file,auto);
  }
  public String readFile(String filePath) {
    return upYunClient.readFile(filePath);
  }
  public boolean readFile(String filePath,File file) {
    return upYunClient.readFile(filePath,file);
  }
  /**
   * 获取文件扩展名
   *
   * @return string
   */
  public String getFileExt(String fileName) {
    return fileName.substring(fileName.lastIndexOf("."));
  }

  /**
   * 依据原始文件名生成新文件名
   * @return
   */
  public String getNewFileName(String fileName) {
    Random random = new Random();
    return "" + random.nextInt(10000)+ System.currentTimeMillis() + this.getFileExt(fileName);
  }

  /**
   * /{year}/{mon}/{day}/{shopId}/{hour}{min}{sec}-{filemd5}{.suffix}
   * @param shopId
   * @param fileName
   * @return
   */
  public String generateUploadImagePath(Long shopId,String fileName){
    String newFileName = getNewFileName(fileName);
    try {
      return "/" + DateUtil.getNowTimeStr(DateUtil.YEAR_MONTH_DATE_2) +"/" + shopId +  "/" + DateUtil.getNowTimeStr("HHmmss") + "-" + UpYun.md5(newFileName)+newFileName;
    } catch (Exception e) {
    }
    return null;
  }
}

