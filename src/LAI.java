import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.File;

public class LAI {
     
	public boolean productLAI(String path)
	{
		//检验产品是否存在
		String product=path+System.getProperty("file.separator")+"LAT.tif";
		File f=new File(product);
	    if(f.exists())
	    {
	    	return false;
	    }
	    
	    //检验依赖产品存在性  NDVI.tif
	    String rely=path+System.getProperty("file.separator")+"NDVI.tif";
	    File relyFile=new File(rely);
	    if(!relyFile.exists())
	    {
	    	return false;
	    }
	    
	    //得到背景参数
	    String parent=relyFile.getParent();
	    File parentF=new File(parent);
	    File parentFF=new File(parentF.getParent());
	    String parentBackData=parentFF.getParent()+System.getProperty("file.separator")+"BackData"+System.getProperty("file.separator")+"LAI_BackData.xml";
	    File backData=new File(parentBackData);
	    if(!backData.exists())
	    {
	    	return false;
	    }
	    
        //叶面积指数二次方系数
        String iC1 = null;
        //面积指数一次方系数
        String iC2 = null;
        //叶面积指数常系数
        String iC3 = null;
        ReadXml re=new ReadXml();
        iC1=re.getNodeValue(parentBackData, "/root/叶面积指数二次方系数");
        iC2=re.getNodeValue(parentBackData, "/root/叶面积指数一次方系数");
        iC3=re.getNodeValue(parentBackData, "/root/叶面积指数常系数");
        
        float iL1 = Float.parseFloat(iC1);
        float iL2 = Float.parseFloat(iC2);
        float iL3 = Float.parseFloat(iC3);
        float[] iNDVI=null;
        String[] option=null;
        int iBlockCount = 1;
        
        //gdal.AllRegister();
        Driver getDriver = gdal.GetDriverByName("GTiff");
        Dataset ndvi= gdal.Open(relyFile.getPath(), gdalconstConstants.GA_ReadOnly);
        
        float[] iLAI = null;
        for (int k = 0; k < iBlockCount; k++)
        {
        	Band readBand = ndvi.GetRasterBand(1);
            iNDVI = new float[ndvi.getRasterXSize() * ndvi.getRasterYSize()];
            readBand.ReadRaster(0, 0, ndvi.getRasterXSize(), ndvi.getRasterYSize(), iNDVI);      	
            readBand.delete();
        	
            iLAI=new float[iNDVI.length];
            for (int i = 0; i < iNDVI.length; i++)
            {
                iLAI[i] = iL1 * (iNDVI[i] * iNDVI[i]) + iL2 * iNDVI[i] - iL3;
                if (iNDVI[i] == -2)
                {
                    iLAI[i] = 2.0f;
                }
            }        
        }
        
      //Dataset dataSet =getDriver.Create(product, ndvi.getRasterXSize(), ndvi.getRasterYSize(), 1, null);
        Dataset dataSet=getDriver.Create(product, ndvi.getRasterXSize(), ndvi.getRasterYSize(), 1, gdalconstConstants.GDT_Float32, option);
        Band writeBand = dataSet.GetRasterBand(1);
        writeBand.WriteRaster(0, 0, ndvi.getRasterXSize(), ndvi.getRasterYSize(), iLAI);
        dataSet.FlushCache();
        dataSet.delete();
        writeBand.delete();
        ndvi.delete();
        
		return true;
	}
}
