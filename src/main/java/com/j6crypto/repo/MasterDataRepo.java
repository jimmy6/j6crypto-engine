package com.j6crypto.repo;

import com.j6crypto.engine.entity.MasterData;
import com.j6crypto.engine.entity.MasterDataKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterDataRepo extends JpaRepository<MasterData, MasterDataKey> {

  public List<MasterData> findByMasterDataKeyCategoryOrderBySequence(MasterDataKey.Category category);

  public List<MasterData> findByMasterDataKeyCategoryAndActiveOrderBySequence(MasterDataKey.Category category, Boolean active);

  public MasterData findByMasterDataKey(MasterDataKey masterDataKey);

  public boolean existsById(MasterDataKey masterDataKey);
}
