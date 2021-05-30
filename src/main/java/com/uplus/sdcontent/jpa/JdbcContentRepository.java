package com.uplus.sdcontent.jpa;

import com.uplus.sdcontent.dto.ContentDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class JdbcContentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcContentRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Optional<ContentDto> findByContentId(String contentId) {
        List<ContentDto> result
                = jdbcTemplate.query("select content_id, content_name, url, creator, cp, category  from contents where content_id = ?",
                executeDtoRowMapper(),
                contentId);
        return result.stream().findAny();
    }

    public void deleteByContentId(String contentId){
        jdbcTemplate.update("delete from contents where content_id = ?", contentId);
    }


    private RowMapper<ContentDto> executeDtoRowMapper() {
        return (rs, rowNum) -> {
            ContentDto contentDto = new ContentDto();
            contentDto.setContentId(rs.getString("content_id"));
            contentDto.setContentName(rs.getString("content_name"));
            contentDto.setUrl(rs.getString("url"));
            contentDto.setCreator(rs.getString("creator"));
            contentDto.setCp(rs.getString("cp"));
            contentDto.setCategory(rs.getString("category"));

            return contentDto;
        };
    }

}
