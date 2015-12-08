DELETE FROM image_version_config;

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081001, 1, 1, 0, 'version.1', 'ENABLED', -1, 'UNCHANGED', NULL, NULL, NULL, 100, 5, 5, 65, 0, 1, 1, 'RIGHT_BOTTOM', '水印大图', 'DEFAULT', '所有原图上传处理');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081002, 1, 1, 0, 'version.2', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 60, 60, NULL, NULL, NULL, NULL, 65, 0, 1, 0, NULL, NULL, 'DEFAULT','60X60');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081003, 1, 1, 0, 'version.3', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 35, 35, NULL, NULL,NULL, NULL, 65, 0, 1, 0, NULL, NULL, 'DEFAULT','35X35');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081004, 1, 1, 0, 'version.4', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 200, 200, NULL, 100, 5, 5, 65, 0, 1, 1, 'RIGHT_BOTTOM', '水印大图', 'DEFAULT','200X200');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081005, 1, 1, 0, 'version.5', 'ENABLED', -1, 'FIX_WIDTH', 720, NULL, NULL, 100, 5, 5, 65, 0, 1, 1, 'RIGHT_BOTTOM', '水印大图', 'DEFAULT', '720XAUTO');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081006, 1, 1, 0, 'version.6', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 80, 80, NULL, 100, 5, 5, 65, 0, 1, 1, 'RIGHT_BOTTOM', '水印小图', 'DEFAULT', '80X80');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081007, 1, 1, 0, 'version.7', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 285, 180, NULL, 100, 5, 5, 65, 0, 1, 1, 'RIGHT_BOTTOM', '水印大图', 'DEFAULT','285X180');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081008, 1, 1, 0, 'version.8', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 360, 240, NULL, 100, 5, 5, 65, 0, 1, 1, 'RIGHT_BOTTOM', '水印大图', 'DEFAULT','360X240');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081009, 1, 1, 0, 'version.9', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 600, 400, NULL, 100, 5, 5, 65, 0, 1, 1, 'RIGHT_BOTTOM', '水印大图', 'DEFAULT','600X400');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081010, 1, 1, 0, 'version.10', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 70, 50, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '70X50');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081011, 1, 1, 0, 'version.11', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 100, 80, NULL, 100, 5, 5, 65, 0, 1, 1, 'RIGHT_BOTTOM', '水印小图', 'DEFAULT', '100X80');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`) 
VALUES (1000001001081012, 1, 1, 0, 'version.12', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 120, 100, NULL, 100, 5, 5, 65, 0, 1, 1, 'RIGHT_BOTTOM', '水印小图', 'DEFAULT', '120X100');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081013, 1, 1, 0, 'version.13', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 110, 110, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '110X110');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081014, 1, 1, 0, 'version.14', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 63, 63, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '63X63');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081015, 1, 1, 0, 'version.15', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 94, 94, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '94X94');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081016, 1, 1, 0, 'version.16', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 109, 109, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '109X109');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081017, 1, 1, 0, 'version.17', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 125, 125, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '125X125');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081018, 1, 1, 0, 'version.18', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 141, 141, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '141X141');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081019, 1, 1, 0, 'version.19', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 164, 164, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '164X164');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081020, 1, 1, 0, 'version.20', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 245, 245, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '245X245');

INSERT INTO `image_version_config` (`id`, `created`, `last_update`, `version`, `name`, `status`, `shop_id`, `thumbnails_type`, `size_fix_width`, `size_fix_height`, `lessen_value`, `watermark_opacity`, `watermark_margin_x`, `watermark_margin_y`, `quality`, `gif2jpg_thumb`, `need_sharpen`, `need_watermark`, `watermark_position`, `watermark_name`, `format`, `description`)
VALUES (1000001001081021, 1, 1, 0, 'version.21', 'ENABLED', -1, 'FIX_WIDTH_OR_HEIGHT', 218, 218, NULL, NULL, NULL, NULL, 65, 0, 1, 0,NULL, NULL, 'DEFAULT', '218X218');
