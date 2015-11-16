package com.will_code_for_food.crucentralcoast.model.common.components;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.will_code_for_food.crucentralcoast.model.getInvolved.MinistryTeam;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by MasonJStevenson on 11/16/2015.
 *
 * A fake Database API with hardcoded values
 */
public class FakeDB {

    //gets a single hard coded ministry team as a JsonObject
    public JsonObject ministryTeamJson() {
        //Gson gsonObj = new Gson();
        JsonParser parser = new JsonParser();
        String ministryTeam = "{ \"_id\" : ObjectId(\"563b0a832930ae0300fbc0a8\"), \"slug\" : \"womens-team\", \"parentMinistry\" : ObjectId(\"563b07402930ae0300fbc09b\"), \"description\" : \"A passionate community of women encouraging each other to be fully surrendered to our King through prayer, worship, and gatherings.\", \"name\" : \"Women's Team\", \"__v\" : 0 }";
        //ArrayList toReturn = gsonObj.fromJson(ministryTeam, ArrayList.class);
        return parser.parse(ministryTeam).getAsJsonObject();
    }

    //returns a single hard coded MinistryTeam as a MinistryTeam object
    public MinistryTeam getMinistryTeam() {
        JsonObject mt = ministryTeamJson();

        String id = mt.get("_id").getAsString();
        id = id.substring(10); //remove ObjectId("
        id = id.substring(0, id.length() - 2); //remove ")
        MinistryTeam parent = null;
        String description = mt.get("description").getAsString();
        String name = mt.get("name").getAsString();

        return new MinistryTeam(id, parent, description, name);
    }

    public Collection<MinistryTeam> getMinistryTeams() {
        return null;
    }
}

/*
{ "_id" : ObjectId("563b0a292930ae0300fbc0a6"), "slug" : "veritas-forum", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "Help plan the Veritas Forum event in January: a dialogue between a MIT physics professor and a Cal Poly professor on science and faith.", "name" : "Veritas Forum", "__v" : 0 }
{ "_id" : ObjectId("563b0a612930ae0300fbc0a7"), "slug" : "weekly-meeting", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "A team passionate about serving the Cru Central Coast family, connecting students to Jesus, and connecting with one another. We plan the Cru Weekly Meeting each week on Tuesday nights.", "name" : "Weekly Meeting", "__v" : 0 }
{ "_id" : ObjectId("563b0a832930ae0300fbc0a8"), "slug" : "womens-team", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "A passionate community of women encouraging each other to be fully surrendered to our King through prayer, worship, and gatherings.", "name" : "Women's Team", "__v" : 0 }
{ "_id" : ObjectId("563b0aa42930ae0300fbc0a9"), "slug" : "global-vision", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "The Global Vision Team’s desire is to capture every student’s attention to God’s heart for the world, by sending students on mission locally and abroad.", "name" : "Global Vision", "__v" : 0 }
{ "_id" : ObjectId("563b0ac22930ae0300fbc0aa"), "slug" : "outreach", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "Outreach team exists to help train, equip, pray for, and encourage followers of Jesus Christ to share their faith.", "name" : "Outreach", "__v" : 0 }
{ "_id" : ObjectId("563b0aed2930ae0300fbc0ab"), "slug" : "community", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "Our goal is to encourage, inspire, and foster the growth of community by planning and executing events of all sizes throughout the year. These events bring people together and create a safe and comfortable environment to grow deeper in a relationship with Jesus Christ. Also, we bring the fun.", "name" : "Community", "__v" : 0 }
{ "_id" : ObjectId("563b0b162930ae0300fbc0ac"), "slug" : "prayer", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "Our mission as prayer team is to put on prayer events in order to encourage a culture of prayer as a way to experience, connect with, and learn from God within the Cru community.", "name" : "Prayer", "__v" : 0 }
{ "_id" : ObjectId("563b0b2e2930ae0300fbc0ad"), "slug" : "web", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "Web Team desires every student to be connected to what is going on in Cru Central Coast by informing them through Facebook, Instagram, Twitter, and the website.", "name" : "Web", "__v" : 0 }
{ "_id" : ObjectId("563b0b452930ae0300fbc0ae"), "slug" : "graphic-design", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "Our purpose is to provide graphic design for the different needs of SLO Cru. These designs can include: slides, T-shirts, programs, flyers, and much more.", "name" : "Graphic Design", "__v" : 0 }
{ "_id" : ObjectId("563b0b5f2930ae0300fbc0af"), "slug" : "mens-team", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "Our mission as Men's team is to inform, challenge and encourage the men at Cal Poly to be more like Jesus, by walking along side them as we learn how to be more of a Man of God.", "name" : "Men's Team", "__v" : 0 }
{ "_id" : ObjectId("563b0b7e2930ae0300fbc0b0"), "slug" : "worship-and-sound", "parentMinistry" : ObjectId("563b07402930ae0300fbc09b"), "description" : "We are a group of Christ-followers seeking to encourage the larger group into experiencing and expressing adoration for God through song and praise. We play worship music and are in charge of doing sound for all Cru events.", "name" : "Worship & Sound", "__v" : 0 }
{ "_id" : ObjectId("563b0c472930ae0300fbc0b1"), "slug" : "social-media", "parentMinistry" : ObjectId("563b090b2930ae0300fbc0a2"), "description" : "Social Media Team desires every student to be connected to what is going on in Cuesta Cru by informing them through Facebook, Instagram, and Twitter.", "name" : "Social Media", "__v" : 0 }
{ "_id" : ObjectId("563b0c892930ae0300fbc0b2"), "slug" : "evangelism", "parentMinistry" : ObjectId("563b090b2930ae0300fbc0a2"), "description" : "Evangelism Team exists to help train, equip, pray for, and encourage followers of Jesus Christ to share their faith. We meet weekly to talk with people on campus.", "name" : "Evangelism", "__v" : 0 }
{ "_id" : ObjectId("563b0cd22930ae0300fbc0b3"), "slug" : "large-group", "parentMinistry" : ObjectId("563b08482930ae0300fbc09c"), "description" : "Large Group Team exists to plan Epic's Thursday night meeting each week. ", "name" : "Large Group", "__v" : 0 }
{ "_id" : ObjectId("563b0d252930ae0300fbc0b4"), "slug" : "community-team", "parentMinistry" : ObjectId("563b08482930ae0300fbc09c"), "description" : "Community Team focuses on planning events for Epic. We also plan men's and women's events throughout the year.", "name" : "Community Team", "__v" : 0 }
{ "_id" : ObjectId("563b0d602930ae0300fbc0b5"), "slug" : "worship", "parentMinistry" : ObjectId("563b08482930ae0300fbc09c"), "description" : "Worship Team desires to bring people into the presence of the Lord through song. We do this at Epic's Large Group each week.", "name" : "Worship", "__v" : 0 }
 */
