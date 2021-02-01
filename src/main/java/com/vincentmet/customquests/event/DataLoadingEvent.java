package com.vincentmet.customquests.event;

import net.minecraftforge.eventbus.api.Event;

public class DataLoadingEvent extends Event{
    public static class Pre extends DataLoadingEvent{}
    public static class Post extends DataLoadingEvent{}
}
