public class CCDToal {

	public String productCCD(String path)
	{   
		//gdal.AllRegister();	
		CCDToal_HJ ccd_HJ=new CCDToal_HJ();
		String result=ccd_HJ.productCCD_HJ(path);
		
		return result;
	}
}
