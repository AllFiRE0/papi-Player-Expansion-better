# Player Expansion 2.1.2

Расширение PlaceholderAPI, идентификатор `player`. Собрано и проверено на Paper 26.3 (build 226)
с PlaceholderAPI 2.12.3.

Подсказки работают в `/papi parse <ник> %player_`: расширение отдаёт полный список через
`getPlaceholders()`, поэтому в автодополнении видны и старые, и новые плейсхолдеры, включая
варианты с аргументами (`<effect>`, `<permission>`, `<format>`, `<attribute>` и т. д.).

## Старые плейсхолдеры

```text
%player_allow_flight%
%player_armor_helmet_name%
%player_armor_helmet_data%
%player_armor_helmet_durability%
%player_armor_chestplate_name%
%player_armor_chestplate_data%
%player_armor_chestplate_durability%
%player_armor_leggings_name%
%player_armor_leggings_data%
%player_armor_leggings_durability%
%player_armor_boots_name%
%player_armor_boots_data%
%player_armor_boots_durability%
%player_bed_x%
%player_bed_y%
%player_bed_z%
%player_bed_world%
%player_biome%
%player_biome_capitalized%
%player_block_underneath%
%player_can_pickup_items%
%player_colored_ping%
%player_compass_world%
%player_compass_x%
%player_compass_y%
%player_compass_z%
%player_custom_name%
%player_current_exp%
%player_direction%
%player_direction_xz%
%player_displayname%
%player_list_name%
%player_exp%
%player_exp_to_level%
%player_first_join_date%
%player_first_played%
%player_first_join%
%player_first_played_formatted%
%player_fly_speed%
%player_food_level%
%player_gamemode%
%player_has_empty_slot%
%player_has_played_before%
%player_empty_slots%
%player_has_health_boost%
%player_has_potioneffect_<effect>%
%player_has_permission_<permission>%
%player_health%
%player_health_boost%
%player_health_rounded%
%player_health_scale%
%player_ip%
%player_online%
%player_is_whitelisted%
%player_is_banned%
%player_is_flying%
%player_is_sneaking%
%player_is_sprinting%
%player_is_sleeping%
%player_is_inside_vehicle%
%player_is_op%
%player_item_in_hand%
%player_item_in_hand_name%
%player_item_in_hand_data%
%player_item_in_hand_durability%
%player_item_in_hand_level_<enchantment>%
%player_item_in_offhand%
%player_item_in_offhand_name%
%player_item_in_offhand_data%
%player_item_in_offhand_durability%
%player_item_in_offhand_level_<enchantment>%
%player_locale%
%player_locale_display_name%
%player_locale_short%
%player_locale_country%
%player_locale_display_country%
%player_last_damage%
%player_last_played%
%player_last_join%
%player_last_played_formatted%
%player_last_join_date%
%player_level%
%player_light_level%
%player_max_air%
%player_max_health%
%player_max_health_rounded%
%player_max_no_damage_ticks%
%player_minutes_lived%
%player_name%
%player_no_damage_ticks%
%player_ping%
%player_ping_<playername>%
%player_remaining_air%
%player_saturation%
%player_seconds_lived%
%player_sleep_ticks%
%player_thunder_duration%
%player_ticks_lived%
%player_time%
%player_time_offset%
%player_total_exp%
%player_uuid%
%player_walk_speed%
%player_weather_duration%
%player_world%
%player_world_type%
%player_world_time_12%
%player_world_time_24%
%player_x%
%player_y%
%player_z%
%player_yaw%
%player_pitch%
%player_absorption%
```

## Новые плейсхолдеры

```text
%player_bed_is_set%
%player_death_x%
%player_death_y%
%player_death_z%
%player_death_world%
%player_time_since_last_played%
%player_time_since_last_join%
%player_last_played_<format>%
%player_last_join_<format>%
%player_lived_<format>%
%player_last_level%
%player_elytra_speed%
%player_last_damage_player%
%player_last_death%
%player_last_death_seconds%
%player_last_death_time%
%player_last_death_name%
%player_last_combat%
%player_last_combat_seconds%
%player_last_kill%
%player_last_kill_seconds%
%player_is_flying_seconds%
%player_is_elytra%
%player_is_gliding%
%player_is_elytra_seconds%
%player_is_sleeping_seconds%
%player_is_sneaking_seconds%
%player_is_sprinting_seconds%
%player_is_bed%
%player_is_bed_seconds%
%player_is_lay%
%player_is_lay_seconds%
%player_is_crawling%
%player_is_crawling_seconds%
%player_is_sit%
%player_is_sit_seconds%
%player_is_walk%
%player_is_walk_seconds%
%player_is_license%
%player_has_potioneffects%
%player_has_potioneffects_<number>%
%player_potioneffects_lvl_<number>%
%player_potioneffect_lvl_<effect>%
%player_potion_effect_level_<effect>%
%player_has_potion_effect_<effect>%
%player_has_unlocked_recipe_<recipe>%
%player_recipes_list%
%player_attribute_<attribute>%
%player_block_looking_at%
%player_block_looking_at_<part>%
%player_block_looking_at_data_<key>%
%player_block_looking_at:<distance>_<part>%
%player_block_looking_at:<distance>_data_<key>%
```

## Прочие существовавшие плейсхолдеры

Эти были в расширении и раньше, но не входили в присланный список старых:

```text
%player_server%
%player_servername%
%player_x_long%
%player_y_long%
%player_z_long%
%player_world_time%
%player_is_conversing%
%player_is_dead%
%player_is_leashed%
```

`%player_server%` и `%player_servername%` оставлены заглушками и возвращают сообщение о том,
что эти данные теперь в expansion `server`.

## Время в игре и форматы

| Плейсхолдер | Значение |
|---|---|
| `%player_lived_seconds%` | всего секунд в игре |
| `%player_lived_<format>%` | то же в заданном формате |
| `%player_last_played_<format>%` | сколько прошло с последнего входа |
| `%player_last_join_<format>%` | то же самое |
| `%player_time_since_last_played%` | секунд с последнего входа |
| `%player_time_since_last_join%` | секунд с последнего входа |
| `%player_minutes_lived%` | минуты в игре (старый алиас) |

Форматы длительности: только цифры и разделители. Токены `DD` `HH` `MM` `SS` (с ведущим нулём)
или `D` `H` `M` `S`; регистр не важен. `seconds` — алиас `SS`.

| Формат | 183845 секунд (2 д 3 ч 4 мин 5 с) |
|---|---|
| `SS` | `183845` |
| `MM` | `3064` |
| `HH` | `51` |
| `DD` | `2` |
| `DD:HH:MM:SS` | `02:03:04:05` |
| `HH:MM:SS` | `51:04:05` |
| `MM:SS` | `3064:05` |
| `DD days HH:MM:SS` | `02 days 03:04:05` |

Один токен = суммарное значение, несколько = компоненты. Первый (левый) токен всегда суммарный,
поэтому `HH:MM:SS` показывает часы сверх суток. Группы букв длиннее двух символов считаются
текстом и не заменяются.

## Состояния и таймеры

Таймеры показывают, сколько секунд игрок **сейчас** находится в состоянии (`0`, если не находится).
Сбрасываются при выходе игрока и при перезапуске сервера.

| Состояние | Флаг | Секунды |
|---|---|---|
| Спринт | `%player_is_sprinting%` | `%player_is_sprinting_seconds%` |
| Реальный Shift-присед | `%player_is_sneaking%` | `%player_is_sneaking_seconds%` |
| Сон | `%player_is_sleeping%` | `%player_is_sleeping_seconds%` |
| Полёт (креатив/флай) | `%player_is_flying%` | `%player_is_flying_seconds%` |
| Элитры | `%player_is_elytra%` / `%player_is_gliding%` | `%player_is_elytra_seconds%` |
| Лежит в кровати | `%player_is_bed%` | `%player_is_bed_seconds%` |
| Лежит на спине (не в кровати) | `%player_is_lay%` | `%player_is_lay_seconds%` |
| Ползёт на животе | `%player_is_crawling%` | `%player_is_crawling_seconds%` |
| Сидит (`/sit`, подушка) | `%player_is_sit%` | `%player_is_sit_seconds%` |
| Идёт пешком | `%player_is_walk%` | `%player_is_walk_seconds%` |

Уточнения:

* `is_sneaking` — только настоящий Shift-присед (`isSneaking()` + поза `SNEAKING`), ползание не считается.
* `is_bed` — игрок в кровати и под ним/в нём реальный блок кровати.
* `is_lay` — поза `SLEEPING`, но **не** в кровати, либо поза GSit `LAY`/`BELLYFLOP`.
* `is_crawling` — поза `SWIMMING` вне воды или `GSitAPI.isPlayerCrawling()`.
* `is_sit` — поза `SITTING`, транспорт `CUSHION`/`SEAT`/`ARMOR_STAND` или `GSitAPI.isEntitySitting()`.
* `is_walk` — горизонтальное движение 0.05–2 блока за тик, на земле, обычная стойка, без спринта,
  приседа, полёта, элитр, езды, сидения, лежания и сна.
* `is_license` — `yes`, если UUID версии 4 (лицензия), иначе `no`. Корректно только если UUID
  приходят от прокси с `online-mode=true`; при `online-mode=false` без форвардинга всегда `no`.

## Бой, смерти и координаты смерти

| Плейсхолдер | Значение |
|---|---|
| `%player_last_damage_player%` | кто последним нанёс урон |
| `%player_last_death%` / `%player_last_death_seconds%` | секунд с последней смерти |
| `%player_last_death_time%` | дата последней смерти (формат PlaceholderAPI) |
| `%player_last_death_name%` | от кого умер (игрок, моб или пусто) |
| `%player_last_combat%` | имя последнего противника в бою |
| `%player_last_combat_seconds%` | секунд с последнего боя |
| `%player_last_kill%` | имя последней жертвы |
| `%player_last_kill_seconds%` | секунд с последнего убийства |
| `%player_death_x%` `%player_death_y%` `%player_death_z%` | координаты последней смерти |
| `%player_death_world%` | мир последней смерти |

Данные боя и смертей хранятся в памяти: после перезапуска сервера обнуляются.

## Зелья

| Плейсхолдер | Значение |
|---|---|
| `%player_has_potioneffect_<effect>%` | `yes` / `no` |
| `%player_has_potion_effect_<effect>%` | `yes` / `no` |
| `%player_potioneffect_lvl_<effect>%` | уровень эффекта или пусто |
| `%player_potion_effect_level_<effect>%` | уровень эффекта или `0` |
| `%player_has_potioneffects%` | `speed 2 haste 1 ...` |
| `%player_has_potioneffects_<number>%` | имя N-го эффекта |
| `%player_potioneffects_lvl_<number>%` | уровень N-го эффекта |

Уровни 1-based (`amplifier + 1`). Имена — ключи реестра (`speed`, `minecraft:strength`, `haste`).
Список отсортирован по ключу, поэтому нумерация стабильна.

## Блок, на который смотрит игрок

`%player_block_looking_at[:<дистанция>[:<fluids|sources>]]_<часть>%`

| Часть | Значение |
|---|---|
| `type` | `STONE` (по умолчанию) |
| `x` `y` `z` | координаты блока |
| `world` | мир |
| `direction` | `NORTH`, `EAST`, ... |
| `data` | все свойства блока: `facing=north,waterlogged=true` |
| `data_<индекс>` | свойство по индексу (`data_0`) |
| `data_<ключ>` | свойство по имени (`data_facing`) |

Примеры: `%player_block_looking_at_type%`, `%player_block_looking_at:20_type%`,
`%player_block_looking_at:10:fluids_data_waterlogged%`.

## Рецепты

| Плейсхолдер | Значение |
|---|---|
| `%player_has_unlocked_recipe_<recipe>%` | `yes` / `no` |
| `%player_recipes_list%` | список ключей через запятую |

## Прочее

| Плейсхолдер | Значение |
|---|---|
| `%player_last_level%` | последний полученный уровень |
| `%player_fly_speed%` | `Player#getFlySpeed()` (по умолчанию `0.05`) |
| `%player_elytra_speed%` | скорость в блоках/сек, 2 знака |
| `%player_attribute_<attribute>%` | значение атрибута со всеми модификаторами |

`%player_attribute_...%` принимает `minecraft:attack_speed`, `attack_speed`, `GENERIC_ATTACK_SPEED`
или `generic.attack_speed`. Неизвестный атрибут → пусто.

## Локаль

| Плейсхолдер | Значение |
|---|---|
| `%player_locale%` | `ru_ru` |
| `%player_locale_short%` | `ru` |
| `%player_locale_country%` | `RU` |
| `%player_locale_display_country%` | `Россия` |
| `%player_locale_display_name%` | `русский (Россия)` |

## Совместимость

Расширение собрано под Java 8 (bytecode 52) и проверено двумя сборками: против Spigot API 1.21.10
и против Paper API 26.3.0 (build 226). На Paper 26.3 стартует без ошибок, `/papi reload`
не оставляет дубликатов задач и слушателей.
