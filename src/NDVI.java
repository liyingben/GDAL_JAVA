import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.File;

public class NDVI {

	public boolean productNDVI(String filePath){
		
		//gdal.AllRegister();
		String strProductName = "NDVI.tif";
        String strSavePath, strB3, strB4;
        String rootPath=filePath+System.getProperty("file.separator");
        String[] productRely={"CCDTOAL_B4.tif","CCDTOAL_B3.tif"};
        //判断产品是否存在
        strSavePath=filePath+System.getProperty("file.separator")+strProductName;
        File file =new File(strSavePath);
        if(file.exists())
        {
        	file.delete();
        	file=null;
        }
        
        //判断依赖产品是否存在
        for(int i=0;i<2;i++){
        	File ccd_file= new File(rootPath+productRely[i]);
        	if(!ccd_file.exists()){
        		return false;
        	}
        }
        
        strB4=rootPath+"CCDTOAL_B4.tif";
        strB3=rootPath+"CCDTOAL_B3.tif";
        
        //判断背景参数是否存在
        File backData=new File(filePath);
        backData=new File(backData.getParent());
        String parentBackData=backData.getParent()+System.getProperty("file.separator")+"BackData"+System.getProperty("file.separator")+"NDVI_BackData.xml";
        File backDataFile=new File(parentBackData);
        if(!backDataFile.exists())
        {
        	return false;
        }
        
        Dataset hDataStrB3= gdal.Open(strB3, gdalconstConstants.GA_ReadOnly);
        Dataset hDataStrB4= gdal.Open(strB4, gdalconstConstants.GA_ReadOnly);
        
        Dataset hDataSave=hDataStrB3;
        
        int iBlockCount = 1;
        float[] iNDVI = null;
        int i, j;
        float[] iB4 = null, iB3 = null;
        Driver getDriver = gdal.GetDriverByName("GTiff");
        String[] option=null;
        
        //计算NDVI
        for (int k = 0; k < iBlockCount; k++)
        {
        	Band readBandB3 = hDataStrB3.GetRasterBand(1);
        	iB3 = new float[hDataStrB3.getRasterXSize() * hDataStrB3.getRasterYSize()];
        	
        	readBandB3.ReadRaster(0, 0, hDataStrB3.getRasterXSize(), hDataStrB3.getRasterYSize(), iB3);      	
        	readBandB3.delete();
            
        	Band readBandB4 = hDataStrB4.GetRasterBand(1);
            iB4 = new float[hDataStrB4.getRasterXSize() * hDataStrB4.getRasterYSize()];
        	
        	readBandB4.ReadRaster(0, 0, hDataStrB4.getRasterXSize(), hDataStrB4.getRasterYSize(), iB4);
        	readBandB4.delete();

            iNDVI = new float[iB4.length];

            for (i = 0; i < iB4.length; i++)
            {
                iNDVI[i] =((iB4[i] - iB3[i]) / (iB4[i] + iB3[i]));
            }


            for (j = 0; j < iB4.length; j++)
            {
                if ((iB4[j] == 0F) || ((iNDVI[j] < -1F) || (iNDVI[j] > 1)))
                {
                    iNDVI[j] = -2F;
                }
            }
            //保存结果
            //Dataset dataSet =getDriver.Create(strSavePath, hDataSave.getRasterXSize(), hDataSave.getRasterYSize(), 1, null);
             Dataset dataSet=getDriver.Create(strSavePath, hDataSave.getRasterXSize(), hDataSave.getRasterYSize(), 1, gdalconstConstants.GDT_Float32, option);
             Band writeBand = dataSet.GetRasterBand(1);
             writeBand.WriteRaster(0, 0, hDataSave.getRasterXSize(), hDataSave.getRasterYSize(), iNDVI);
             //dataSet.WriteRaster(0, 0, hDataSave.getRasterXSize(), hDataSave.getRasterYSize(), hDataSave.getRasterXSize(), hDataSave.getRasterYSize(), gdalconstConstants.GDT_Float32, iNDVI, null);
             dataSet.FlushCache();
             dataSet.delete();
             writeBand.delete();
             
             
        }
        hDataStrB3.delete();
        hDataStrB4.delete();
        //gdal.GDALDestroyDriverManager();  
		return true;
	} 
}
