import java.io.File;


public class FileMethod {
	
	public static File makeNewDirectory(File f){
		File theDir = f;
		String folderPath = theDir.getPath();
		String foderNameStem = theDir.getName();
		
		// to check if there any same foldername
		int i = 0;
		
		while(theDir.exists() && theDir.list().length != 0){
			i++;
			String num = String.format("%02d", i);

			folderPath = FileMethod.getParentPath(theDir)+foderNameStem+"_"+num;
			theDir = new File(folderPath);
		}
		
		// creating new folder
		System.out.println("creating directory: " + folderPath);
		boolean result = theDir.mkdir();  
		if(result) {    
			System.out.println("new directory created");  
		}else{
			System.out.println("couldent make a new directory:" + folderPath);
		}

		return theDir;

	}
	public static File makeNewDirectory(String dirPath, String fldName){
		String folderNameStem = fldName;
		String folderDirectry = dirPath;
		String folderPath = folderDirectry+File.separator+folderNameStem;
		File theDir = new File(folderPath);
		return FileMethod.makeNewDirectory(theDir);
	}
	
	public static String getParentPath(File f){// return with last '/' or '\'
		String absPath = f.getAbsolutePath();
		String name = f.getName();
		return absPath.substring(0, absPath.length()-name.length());
	}
	public static String getParentPath(String path){
		return FileMethod.getParentPath(new File(path));
	}

}
