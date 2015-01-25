import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FolderUtility {
	
	public static File makeNewFolder(String f){//@@@ you can add a vriable to decide what should put after the duplicated folder name
		boolean result = false;
		String folderName = f;
		File newDir = new File(folderName);
		// if the directory does not exist, create it
		if (!newDir.exists()) {
//			System.out.println("creating directory: " + folderName);//@test
			result = newDir.mkdir();  

//			}
		}else{//if the folder name is duplicated, put something after the name
			while(newDir.exists()){
				//put time after the name// // // or you can simply put a serial number after the name
				Date dNow = new Date( );
				SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd_kkmmss");	
				//make new folder name
				folderName = f+ft.format(dNow);
				newDir = new File(folderName);
			}
			result = newDir.mkdir(); 
		}
		if(result){
			return newDir;
		}else{
			return null;
		}
	}
	
	public static File chooseFolder(String f){//choose a folder. -> creat it if it doesn't exist
		boolean result = true;// only if the folder doesn't exist neither couldn't make one result will be -> false;
		String folderName = f;
		File tarDir = new File(folderName);
		// if the directory does not exist, create it
		if (!tarDir.exists()) {
//			System.out.println("creating directory: " + folderName);//@test
			result = tarDir.mkdir();// if it coldn't to be created, then result will be false
		}
		
		if(result){
			return tarDir;
		}else{
			return null;
		}
	}
	
	
}
