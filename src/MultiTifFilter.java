import java.io.File;
import java.io.FileFilter;

public class MultiTifFilter implements FileFilter{

	private String[] nameCon;
	@Override
	public boolean accept(File pathname) {
		// TODO Auto-generated method stub
		String name=pathname.getName();
		if(name.endsWith(".TIF")||name.endsWith(".tif"))
		{
			int i=0;
			for(i=0;i<nameCon.length;i++)
			{
				if(name.contains(nameCon[i])){
					//System.out.println(name.contains(nameCon[i]));
				}
				else
				{
					//System.out.println(name.contains(nameCon[i]));
					break;
				}

			}
			if(i==nameCon.length)
				return true;
			else
				return false;

		}
		return false;
	}
	
	public MultiTifFilter(String name) {
		super();
		// TODO Auto-generated constructor stub
		if(name.contains("#"))
		{
			nameCon=name.split("#");
		}
		else
		{
			nameCon=new String[1];
			nameCon[0]=name;
		}
			
		//nameCon=name;
	}
    
	
}
