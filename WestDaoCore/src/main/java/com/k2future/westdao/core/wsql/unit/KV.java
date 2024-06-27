package com.k2future.westdao.core.wsql.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author west
 * @since 2024/6/25
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class KV<K,V> implements Serializable {


   private static final long serialVersionUID = -3968226353385426567L;

   private K key;

   private V value;

}
