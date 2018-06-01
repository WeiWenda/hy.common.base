package org.hy.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;





/**
 * 对象属性的合并（或拼接）字符串。
 * 
 * 类似于 org.hy.common.SumStringMap，不同是，此类是对对象属性的合并或拼接字符串。
 * 
 * 要求1：对象属性的类型必须是 java.lang.String
 * 要求2：对象属性必须有Public访问域的Geter、Setter方法。
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2018-06-01
 */
public class SumObjectMap<K ,V> extends Hashtable<K ,V> implements Map<K ,V>
{

    private static final long serialVersionUID = -8835763432149887369L;

    /** 连接符。默认是空字符串 */
    private String        connector;
    
    /**  */
    private MethodReflect methodSetter;
    
    private MethodReflect methodGetter;
    
    /** 合并或拼接对象的那个属性。支持面向对象，可实现xxx.yyy.www全路径的解释 */
    private String        methodURL;
    
    
    
    public SumObjectMap()
    {
        this("");
    }
    
    
    
    public SumObjectMap(int i_InitialCapacity)
    {
        this("" ,i_InitialCapacity);
    }
    
    
    
    public SumObjectMap(String i_Connector)
    {
        super();
        
        this.connector = i_Connector;
    }
    
    
    
    public SumObjectMap(String i_Connector ,int i_InitialCapacity)
    {
        super(i_InitialCapacity);
        
        this.connector = i_Connector;
    }
    
    
    
    /**
     * 覆盖性的设置值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-06-01
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Value
     * @return
     */
    public synchronized V set(K i_Key ,V i_Value)
    {
        return super.put(i_Key ,i_Value);
    }
    
    
    
    /**
     * 对象属性的字符串合并（或拼接）
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-06-01
     * @version     v1.0
     *
     * @param i_Key
     * @param i_Value  待合并拼接的对象。当对象NULL或对象合并拼接的属性为NULL时，不合并、不拼接。
     * @return         永远返回Map.Key值相同的首个Map.Value对象
     *
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized V put(K i_Key ,V i_Value)
    {
        V v_Old = super.get(i_Key);
        
        if ( i_Value == null )
        {
            return v_Old;
        }
        else if ( v_Old == null )
        {
            if ( this.methodSetter == null )
            {
                try
                {
                    this.methodSetter = new MethodReflect(i_Value.getClass() ,this.methodURL ,true ,MethodReflect.$NormType_Setter);
                    this.methodGetter = new MethodReflect(i_Value.getClass() ,this.methodURL ,true ,MethodReflect.$NormType_Getter);
                }
                catch (Exception exce)
                {
                    exce.printStackTrace();
                    throw new RuntimeException(exce);
                }
            }
            
            return super.put(i_Key ,i_Value);
        }
        else
        {
            try
            {
                String v_NewValue = (String)this.methodGetter.invokeForInstance(i_Value);
                if ( v_NewValue == null )
                {
                    return v_Old;
                }
                
                String v_OldValue = (String)this.methodGetter.invokeForInstance(v_Old);
                if ( v_OldValue != null )
                {
                    v_NewValue = v_OldValue + this.connector + v_NewValue;
                }
                
                this.methodSetter.invokeSetForInstance(v_Old ,v_NewValue);
                return v_Old;
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
                throw new RuntimeException(exce);
            }
        }
    }
    
    
    
    /**
     * 批量的对象属性的合并拼接字符串 
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-06-01
     * @version     v1.0
     *
     * @param i_AddValues
     *
     * @see java.util.Hashtable#putAll(java.util.Map)
     */
    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> i_AddValues) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends V>> i = i_AddValues.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends V> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }
    
    
    
    /**
     * 批量的覆盖性的设置值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-06-01
     * @version     v1.0
     *
     * @param i_AddValues
     */
    public synchronized void setAll(Map<? extends K, ? extends V> i_AddValues) 
    {
        Iterator<? extends Map.Entry<? extends K, ? extends V>> i = i_AddValues.entrySet().iterator();
        
        while (i.hasNext()) 
        {
            Map.Entry<? extends K, ? extends V> e = i.next();
            set(e.getKey(), e.getValue());
        }
    }


    
    /**
     * 获取：连接符。默认是空字符串
     */
    public String getConnector()
    {
        return connector;
    }
    

    
    /**
     * 设置：连接符。默认是空字符串
     * 
     * @param i_Connector 
     */
    public void setConnector(String i_Connector)
    {
        this.connector = Help.NVL(i_Connector);
    }


    
    /**
     * 获取：合并或拼接对象的那个属性。支持面向对象，可实现xxx.yyy.www全路径的解释
     */
    public String getMethodURL()
    {
        return methodURL;
    }
    


    /**
     * 设置：合并或拼接对象的那个属性。支持面向对象，可实现xxx.yyy.www全路径的解释
     * 
     * @param methodURL 
     */
    public void setMethodURL(String methodURL)
    {
        this.methodURL = methodURL;
    }
    
}