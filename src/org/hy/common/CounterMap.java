package org.hy.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;





/**
 * 计数器Map -- 互联互通Map的子类。
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2014-05-23
 */
public class CounterMap<K> extends InterconnectMap<K ,Long>
{

    private static final long serialVersionUID = 7027323952523562313L;
    
    /** 最小值 */
    private long minValue = 0;
    
    /** 最大值 */
    private long maxValue = 0;
    
    /** 合计值 */
    private long sumValue = 0;
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Count
     * @return
     */
    public synchronized Long set(K i_Key ,Integer i_Count)
    {
        return this.set(i_Key ,i_Count.longValue());
    }
    
    
    
    /**
     * 覆盖型的设置值
     * 
     * @param i_Key
     * @param i_Count
     * @return
     */
    public synchronized Long set(K i_Key ,Long i_Count)
    {
        Long v_Count = i_Count;
        
        if ( v_Count == null )
        {
            v_Count = 0L;
        }
        
        sumValue += v_Count;
                
        if ( minValue > v_Count.intValue() )
        {
            minValue = v_Count;
        }
        
        if ( maxValue < v_Count.intValue() )
        {
            maxValue = v_Count;
        }
        
        Long v_Ret = super.put(i_Key ,v_Count);
        return v_Ret == null ? 0L : v_Ret;
    }
    
    
    
    /**
     * 计数型的递增值
     */
    public synchronized Long put(K i_Key)
    {
        return this.put(i_Key ,1L);
    }
    
    
    
    /**
     * 计数型的递减值
     */
    public synchronized Long putMinus(K i_Key)
    {
        return this.put(i_Key ,-1L);
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    public synchronized Long put(K i_Key ,Integer i_Count)
    {
        return this.put(i_Key ,i_Count.longValue());
    }
    
    
    
    /**
     * 计数型的递增或递减值
     */
    @Override
    public synchronized Long put(K i_Key ,Long i_Count)
    {
        Long v_Count = i_Count;
        if ( v_Count == null )
        {
            v_Count = 0L;
        }
        
        sumValue += v_Count;
        
        if ( this.containsKey(i_Key) )
        {
            // 这个判断是有用的。如果去取除，会影响Jobs的轮询，原因我忘记了。2016-07-15
            if ( v_Count > 0 )
            {
                v_Count = v_Count + super.get(i_Key);
            }
        }
        
        if ( minValue > v_Count.intValue() )
        {
            minValue = v_Count;
        }
        
        if ( maxValue < v_Count.intValue() )
        {
            maxValue = v_Count;
        }
        
        Long v_Ret = super.put(i_Key ,v_Count);
        return v_Ret;
    }
    
    
    
    @Override
    public synchronized void putAll(Map<? extends K, ? extends Long> i_Counters) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Long>> i = i_Counters.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Long> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }
    
    
    
    public synchronized void setAll(Map<? extends K, ? extends Long> i_Counters) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends Long>> i = i_Counters.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends Long> e = i.next();
            set(e.getKey(), e.getValue());
        }
    }
    
    
    
    /**
     * 反向获取 = ，i_Count 指向的所有Key对象
     * 
     * @param value
     * @return
     */
    public synchronized List<K> getReverse_Equal(Long i_Count) 
    {
        return this.getReverse(i_Count);
    }
    
    
    
    /**
     * 反向获取 >= ，i_Count 指向的所有Key对象
     * 
     * @param value
     * @return
     */
    public synchronized List<K> getReverse_GreaterEqual(Long i_Count) 
    {
        return this.getReverse(i_Count ,this.maxValue);
    }
    
    
    
    /**
     * 反向获取 > ，i_Count 指向的所有Key对象
     * 
     * @param value
     * @return
     */
    public synchronized List<K> getReverse_Greater(Long i_Count) 
    {
        return this.getReverse(i_Count + 1 ,this.maxValue);
    }
    
    
    
    /**
     * 反向获取 <= ，i_Count 指向的所有Key对象
     * 
     * @param value
     * @return
     */
    public synchronized List<K> getReverse_LessEqual(Long i_Count) 
    {
        return this.getReverse(this.minValue ,i_Count);
    }
    
    
    
    /**
     * 反向获取 < ，i_Count 指向的所有Key对象
     * 
     * @param value
     * @return
     */
    public synchronized List<K> getReverse_Less(Long i_Count) 
    {
        return this.getReverse(this.minValue ,i_Count - 1);
    }
    
    
    
    /**
     * 反向获取一个范围内 ，i_Count 指向的所有Key对象
     * 
     * @param value
     * @return
     */
    public synchronized List<K> getReverse(Long i_MinCount ,Long i_MaxCount) 
    {
        List<K>        v_Ret      = new ArrayList<K>();
        Iterator<Long> v_Iterator = super.getValues();
        
        while ( v_Iterator.hasNext() )
        {
            Long v_Count = v_Iterator.next();
            
            if ( i_MinCount <= v_Count && v_Count <= i_MaxCount )
            {
                v_Ret.addAll(super.getReverse(v_Count));
            }
        }
        
        return v_Ret;
    }

    
    
    public long getMinValue()
    {
        return minValue;
    }
    


    public long getMaxValue()
    {
        return maxValue;
    }


    
    public long getSumValue()
    {
        return sumValue;
    }
    
    
    
    /**
     * 求某些键值的合计次数
     * 
     * @param i_Keys
     * @return
     */
    public long getSumValue(String [] i_Keys)
    {
        long v_Sum = 0;
        
        if ( Help.isNull(i_Keys) )
        {
            return v_Sum;
        }
        
        for (int i=0; i<i_Keys.length; i++)
        {
            if ( i_Keys[i] != null )
            {
                Long v_Temp = this.get(i_Keys[i]);
                
                if ( v_Temp != null )
                {
                    v_Sum += v_Temp.longValue();
                }
            }
        }
        
        return v_Sum;
    }
    
    
    
    /**
     * 求排除某些键值的其余键值的合计次数
     * 
     * @param i_Keys
     * @return
     */
    public long getSumValueExclude(String [] i_Keys)
    {
        return this.sumValue - this.getSumValue(i_Keys);
    }
    
}
