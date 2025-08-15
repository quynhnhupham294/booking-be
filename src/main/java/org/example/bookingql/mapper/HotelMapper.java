package org.example.bookingql.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bookingql.common.mapper.BaseMapper;
import org.example.bookingql.dto.request.HotelRequest;
import org.example.bookingql.dto.response.HotelResponse;
import org.example.bookingql.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HotelMapper extends BaseMapper<HotelRequest, HotelResponse, Hotel> {

    // Request -> Entity (List<String> -> String JSON)
    @Override
    @Mapping(target = "idHotels", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "hotelImages", target = "hotelImages", qualifiedByName = "mapListToJson")
    Hotel toEntity(HotelRequest requestDto);

    // Entity -> Response (String JSON -> List<String>)
    @Override
    @Mapping(source = "hotelImages", target = "hotelImages", qualifiedByName = "mapJsonToList")
    HotelResponse toResponse(Hotel hotel);

    // --------- Custom convert methods ---------
    @Named("mapListToJson")
    default String mapListToJson(List<String> list) {
        if (list == null) return null;
        try {
            return new ObjectMapper().writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Error converting list to JSON", e);
        }
    }

    @Named("mapJsonToList")
    default List<String> mapJsonToList(String json) {
        if (json == null) return null;
        try {
            return new ObjectMapper().readValue(json, List.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to list", e);
        }
    }
}
