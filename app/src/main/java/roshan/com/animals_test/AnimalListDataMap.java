package roshan.com.animals_test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by roshanmaharjan on 15/08/16.
 */
public class AnimalListDataMap{


    public static HashMap<String, List<Species>> getData() {
        HashMap<String, List<Species>> detailList = new HashMap<String, List<Species>>();


        //Add list of items in dogs category
        List<Species> dogs=new ArrayList<Species>();
        Species germanShephard = new Species();
        germanShephard.setName("German Shephard");
        germanShephard.setDesc("Breed of medium to large-sized working dog that originated in Germany. ");
        germanShephard.setImageId(R.mipmap.ic_dog);

        Species pitBull = new Species();
        pitBull.setName("Pit Bull");
        pitBull.setDesc("Pit bulls were created by breeding bulldogs and terriers together");
        pitBull.setImageId(R.mipmap.ic_dog);

        Species husky = new Species();
        husky.setName("Husky");
        husky.setDesc("The word husky originated from the word referring to Arctic people in general");
        husky.setImageId(R.mipmap.ic_dog);

        dogs.add(0, germanShephard);
        dogs.add(1, pitBull);
        dogs.add(2, husky);

        List<Species> cats=new ArrayList<Species>();
        Species meow = new Species();
        meow.setName("Bob Cat");
        meow.setDesc("The bobcat (Lynx rufus) is a North American cat. ");
        meow.setImageId(R.mipmap.ic_cat);


        Species cheetah = new Species();
        cheetah.setName("Cheetah");
        cheetah.setDesc("Also known as hunting leopard");
        cheetah.setImageId(R.mipmap.ic_cat);
        cats.add(0, meow);
        cats.add(1, cheetah);

        List<Species> birds=new ArrayList<Species>();
        Species kiwi = new Species();
        kiwi.setName("Kiwi");
        kiwi.setDesc("kiwis are flightless birds native to New Zealand");
        kiwi.setImageId(R.mipmap.ic_cock);
        birds.add(0, kiwi);


        detailList.put("Dogs", dogs);
        detailList.put("Cats",cats);
        detailList.put("Birds",birds);

        return detailList;
    }

}
