package com.learnreactivespring.Router;

import com.learnreactivespring.Handler.ItemHandler;
import com.learnreactivespring.constants.ItemConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemRouter {

    @Bean
    public RouterFunction<ServerResponse> itemRouter1(ItemHandler itemHandler){
        return RouterFunctions.route(GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT)
                .and(accept(MediaType.APPLICATION_JSON)),itemHandler::getAllItems)
                .andRoute(GET(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT+"/{id}").and(accept(MediaType.APPLICATION_JSON)),itemHandler::getItemById)
                .andRoute(POST(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT).and(accept(MediaType.APPLICATION_JSON))
                , itemHandler::createOneItem)
                .andRoute(DELETE(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT+"/{id}")
                .and(accept(MediaType.APPLICATION_JSON))
                        ,itemHandler::deleteOneItem)
                .andRoute(PUT(ItemConstants.ITEM_FUNCTIONAL_ENDPOINT+"/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,itemHandler::updateOneItem
                );
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemHandler itemHandler) {
return RouterFunctions
        .route(GET("/fun/runtimeException").and(accept(MediaType.APPLICATION_JSON))
        ,itemHandler::itemsException
        );
    }

    @Bean
    public RouterFunction<ServerResponse> itemStreamRoute(ItemHandler itemHandler){

        return RouterFunctions.route(GET(ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON))
        ,itemHandler::itemsStream);
    }
}