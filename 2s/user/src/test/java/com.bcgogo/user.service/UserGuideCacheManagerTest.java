package com.bcgogo.user.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.BooleanEnum;
import com.bcgogo.common.Pair;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.cache.UserGuideCacheManager;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-28
 * Time: 下午4:52
 */
public class UserGuideCacheManagerTest extends AbstractTest {
  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testUserGuideStepCached() throws Exception {
    List<UserGuideStepDTO> list = new ArrayList<UserGuideStepDTO>();
    UserGuideStepDTO dto = new UserGuideStepDTO();
    dto.setName("1");
    dto.setHead(BooleanEnum.TRUE);
    dto.setTail(BooleanEnum.FALSE);
    dto.setPreviousStep(null);
    list.add(dto);
    dto = new UserGuideStepDTO();
    dto.setName("2-1");
    dto.setHead(BooleanEnum.FALSE);
    dto.setTail(BooleanEnum.TRUE);
    dto.setPreviousStep("1");
    list.add(dto);
    dto = new UserGuideStepDTO();
    dto.setName("2-2");
    dto.setHead(BooleanEnum.FALSE);
    dto.setTail(BooleanEnum.FALSE);
    dto.setPreviousStep("1");
    list.add(dto);
    dto = new UserGuideStepDTO();
    dto.setName("3-1");
    dto.setHead(BooleanEnum.FALSE);
    dto.setTail(BooleanEnum.FALSE);
    dto.setPreviousStep("2-2");
    list.add(dto);
    dto = new UserGuideStepDTO();
    dto.setName("4-1");
    dto.setHead(BooleanEnum.FALSE);
    dto.setTail(BooleanEnum.TRUE);
    dto.setPreviousStep("3-1");
    list.add(dto);
    dto = new UserGuideStepDTO();
    dto.setName("4-2");
    dto.setHead(BooleanEnum.FALSE);
    dto.setTail(BooleanEnum.TRUE);
    dto.setPreviousStep("3-1");
    list.add(dto);
    dto = new UserGuideStepDTO();
    dto.setName("4-3");
    dto.setHead(BooleanEnum.FALSE);
    dto.setTail(BooleanEnum.TRUE);
    dto.setPreviousStep("3-1");
    list.add(dto);
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    userGuideService.saveUserGuideStepList(list);

    Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> pair = UserGuideCacheManager.getUserGuideStepByName("3-1");
    Assert.assertEquals(3, pair.getValue().size());
    dto = UserGuideCacheManager.getPreviousUserGuideStepByName("3-1");
    Assert.assertEquals("2-2", dto.getName());
    dto = UserGuideCacheManager.getPreviousUserGuideStepByName("1");
    Assert.assertNull(dto);
    dto = UserGuideCacheManager.getPreviousUserGuideStepByName("-1");
    Assert.assertNull(dto);
    pair = UserGuideCacheManager.getUserGuideStepByName("3-1");
    Assert.assertEquals("2-2", pair.getKey().getPreviousStep());


  }
}
