package roshan.com.animals_test;


/**
 * Created by roshanmaharjan on 15/08/16.
 */

/**
 *
 * Animal Species class.
 */
public class Species {
    private String name;
    private String desc;
    private int imgId;
    boolean videoUploaded=false;
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public String getDesc()
    {
        return desc;
    }
    public void setDesc(String desc)
    {
        this.desc=desc;
    }
        public  int getImageId()
        {
            return imgId;
        }
        public void setImageId(int imgId)
        {
            this.imgId=imgId;
        }
    public void setVideoUploadFlag(){
        videoUploaded=true;
    }
    public boolean getVideoUploadFlag()
    {
        return  this.videoUploaded;
    }
}
