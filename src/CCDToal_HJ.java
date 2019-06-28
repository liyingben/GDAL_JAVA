import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CCDToal_HJ {
	
	public String productCCD_HJ(String path)
	{
		String[] strSaves = { "", "", "", "" };
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSS");
		String str=sdf.format(new java.util.Date());
//		gdal.AllRegister();

		//设置CCD存储路径
		
		String strCcdPath=path+System.getProperty("file.separator")+"CCD";
		String strResuPath=path+System.getProperty("file.separator")+"Result"+System.getProperty("file.separator")+str;
		File ccdPath=new File(strCcdPath);
		if(!ccdPath.exists()){
			ccdPath.mkdirs();
		}
		File result=new File(strResuPath);
		if(!result.exists()){
		    result.mkdirs();
		}
		
		for(int i=0;i<strSaves.length;i++)
		{
			File file=new File(strResuPath+System.getProperty("file.separator")+"CCDTOAL_B"+(i+1)+".tif");
			if(file.exists())
			{
				file.delete();
			}
			strSaves[i]=file.getPath();
		}
		
		//判断需要定标的卫星
		String strSat = "no";
        String[] strSatellites = { "HJ1A", "HJ1B" };
        File[] iR = { };
        
        for (int ii = 0; ii < strSatellites.length; ii++)
        {
            iR =ccdPath.listFiles(new MultiTifFilter(strSatellites[ii]));
            if (iR.length!= 0)
            {
                strSat = strSatellites[ii];
            }
        }
        
        //判断需要定标的传感器
        String strCen = "no";
        String[] strCensors = { "CCD1", "CCD2" };
        File[] iR1 = { };

        for (int jj = 0; jj < strCensors.length; jj++)
        {
            iR1 = ccdPath.listFiles(new MultiTifFilter(strCensors[jj]));
            if (iR1.length!= 0)
            {
                strCen = strCensors[jj];
            }
        }
        
        //得到需要定标的波段文件
        File[] strTifs = new File[4];
        File[] iR2 = { };

        for (int kk = 0; kk < 4; kk++)
        {
           
        	String regex=strSat+"#"+strCen+"#"+"-"+String.valueOf(kk+1)+".";
        	iR2=ccdPath.listFiles(new MultiTifFilter(regex));
        	if (iR2.length !=0)
            {
                strTifs[kk] = iR2[0];
            }
        }
        
//            for(int kk = 0; kk < 4; kk++)
//            {
//            	if(strTifs[kk].exists())
//            	{
//                    FileInputStream fileInputStream=null;
//                    FileOutputStream fileOutputStream=null;
//					try {
//						fileInputStream = new FileInputStream(strTifs[kk]);
//						fileOutputStream=new FileOutputStream(strResuPath);  
//					} catch (FileNotFoundException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}  
//                    byte[] by=new byte[1024];  
//                    int len;  
//                    try {
//						while((len=fileInputStream.read(by))!=-1)  
//						{  
//						    fileOutputStream.write(by, 0, len);  
//						}
//	                    fileInputStream.close();  
//	                    fileOutputStream.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}  
//
//            	}
//            }

        
        if (strSat == "no" || strCen == "no")
        {
            String rr = "生产空间没有标准影像文件HJ1A/HJ1B;CCD1/CCD2:" + path;
            System.out.println(rr);

            return null;
        }
        
        //判断背景数据的存在性
        String strBackData = path+System.getProperty("file.separator")+"BackData"+System.getProperty("file.separator")+"CCDTOAL_BackData.xml";
        File backData=new File(strBackData);
        if (!backData.exists())
        {
            String rr = "生产空间没有CCDTOAL_BackData.xml文件:" + strBackData;
            System.out.println(rr);
            return null;
        }
        
        //获取背景数据值
        float[] iGains = new float[4];
        float[] iBiass = new float[4];
        String iGain = null;
        String iBias =null;
        String strSatAndCen = strSat + strCen;
        
        for (int ll = 0; ll < 4; ll++)
        {
            //读取XML 给iGains和iBiass赋值
            String xmlPathiGains = "/root/" + strSatAndCen + "B" + String.valueOf(ll + 1)+ "Gain";
            String xmlPathiBiass = "/root/" + strSatAndCen + "B" + String.valueOf(ll + 1) + "Bias";
            ReadXml re=new ReadXml();
            iGain=re.getNodeValue(strBackData, xmlPathiGains);
            iBias=re.getNodeValue(strBackData, xmlPathiBiass);
            iGains[ll] = Float.parseFloat(iGain);
            iBiass[ll] = Float.parseFloat(iBias);
        }
        
        File strTif = null;
        Driver getDriver = gdal.GetDriverByName("GTiff");
        Dataset hDataset=null;
        for (int gg = 0; gg < 4; gg++)
        {
            strTif = strTifs[gg];
            iGain = String.valueOf(iGains[gg]);
            iBias = String.valueOf(iBiass[gg]);

            float[] iDN = new float[] { };
            if (strTif.exists())
            {                
                hDataset= gdal.Open(strTif.getPath(), gdalconstConstants.GA_ReadOnly);
                Band readBand = hDataset.GetRasterBand(1);
                
                iDN = new float[hDataset.getRasterXSize() * hDataset.getRasterYSize()];
                
                readBand.ReadRaster(0, 0, hDataset.getRasterXSize(), hDataset.getRasterYSize(), iDN);
                readBand.ReadRaster(0, 0, hDataset.getRasterXSize(), hDataset.getRasterYSize(), iDN);
                readBand.delete();

                List<Integer> badIndex = new ArrayList<Integer>();
                for (int i = 0; i < iDN.length; i++)
                {
                      if (iDN[i] == 0.0)
                      {
                          badIndex.add(i);
                      }
                      iDN[i] = iDN[i] / Float.parseFloat(iGain)+ Float.parseFloat(iBias);
                }
                for (int j = 0; j < badIndex.size(); j++)
                {
                   iDN[badIndex.get(j)] = 0;
                } 
                String[] option=null;
                //Dataset dataSet =getDriver.Create(strSaves[gg], hDataset.getRasterXSize(), hDataset.getRasterYSize(), 1, null);
                //dataSet=getDriver.Create(strSaves[gg], hDataset.getRasterXSize(), hDataset.getRasterYSize(), 1, gdalconstConstants.GDT_Float32, null);
                Dataset dataSet=getDriver.Create(strSaves[gg], hDataset.getRasterXSize(), hDataset.getRasterYSize(), 1, gdalconstConstants.GDT_Float32, option);

//                GeoInfo geo=new GeoInfo();
//                geo.getInfo(strTif.getPath());
//
//                dataSet.SetProjection(geo.ProjectionRef);
//                dataSet.SetDescription(strSaves[gg]);
//                dataSet.SetGeoTransform(geo.GeoTransform);
//                dataSet.SetMetadataItem("", geo.DefaultMetaData);
//                dataSet.SetMetadataItem("IMAGE_STRUCTURE", geo.ImageStructureMetaData);
//                dataSet.SetMetadataItem("SUBDATASETS", geo.SubdatasetsMetaData);
//                dataSet.SetMetadataItem("GEOLOCATION", geo.GeolocationMetaData);
                
                Band writeBand = dataSet.GetRasterBand(1);
                //WriteRaster(0, 0, hDataset.getRasterXSize(), hDataset.getRasterYSize(), iDN);
                writeBand.WriteRaster(0, 0, hDataset.getRasterXSize(), hDataset.getRasterYSize(), hDataset.getRasterXSize(), hDataset.getRasterYSize(), gdalconstConstants.GDT_Float32, iDN, 0, 0);
                //dataSet.WriteRaster(0, 0, hDataset.getRasterXSize(), hDataset.getRasterYSize(), hDataset.getRasterXSize(), hDataset.getRasterYSize(), gdalconstConstants.GDT_Float32, iDN, null);
                dataSet.FlushCache();
                dataSet.delete();
                writeBand.delete();
                
                hDataset.delete();
            }
        }
        //gdal.GDALDestroyDriverManager();  
		return strResuPath;
	}
}
