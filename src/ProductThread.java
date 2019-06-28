public class ProductThread extends Thread{

	public void run()
	{
		CCDToal ccd=new CCDToal();
		String result=ccd.productCCD("D:\\T20121123004176-0001\\1");
		NDVI nvdi=new NDVI();
		nvdi.productNDVI(result);
		LAI lai=new LAI();
		lai.productLAI(result);
	}
}
