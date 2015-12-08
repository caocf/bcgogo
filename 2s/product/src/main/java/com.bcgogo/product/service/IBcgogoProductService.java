package com.bcgogo.product.service;

import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.product.BcgogoProductDTO;
import com.bcgogo.product.BcgogoProductPropertyDTO;

import java.util.List;

public interface IBcgogoProductService {
  List<BcgogoProductDTO> getBcgogoProductDTOByPaymentType(PaymentType paymentType, Boolean isSimple);

  BcgogoProductDTO getBcgogoProductDTOById(Long id);

  BcgogoProductPropertyDTO getBcgogoProductPropertyDTOById(Long id);

  void saveBcgogoProductDTO(BcgogoProductDTO bcgogoProductDTO);
}