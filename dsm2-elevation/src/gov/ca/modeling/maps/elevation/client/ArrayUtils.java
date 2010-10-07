package gov.ca.modeling.maps.elevation.client;
import com.google.gwt.core.client.JavaScriptObject;

public class ArrayUtils
{


    public static JavaScriptObject toJsArray (int[] array)
    {
        JavaScriptObject result = createArray();
        for (int i = 0; i < array.length; i++) {
            pushArray(result, array[i]);
        }
        return result;
    };


    public static JavaScriptObject toJsArray (double[] array)
    {
        JavaScriptObject result = createArray();
        for (int i = 0; i < array.length; i++) {
            pushArray(result, array[i]);
        }
        return result;
    };

    
    public static JavaScriptObject toJsArray (Object[] array)
    {
        JavaScriptObject result = createArray();
        for (int i = 0; i < array.length; i++) {
            pushArray(result, array[i]);
        }
        return result;
    };

    
    
    private native static JavaScriptObject createArray () /*-{
        return new Array();
    }-*/;

    private native static void pushArray (JavaScriptObject array, int i) /*-{
        array.push(i);
    }-*/;

    private native static void pushArray (JavaScriptObject array, double d) /*-{
        array.push(d);
    }-*/;

    private native static void pushArray (JavaScriptObject array, Object o) /*-{
        array.push(o);
    }-*/;

}
