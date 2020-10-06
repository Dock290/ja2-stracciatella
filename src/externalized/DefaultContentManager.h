#pragma once

#include "game/GameRes.h"

#include "ContentManager.h"
#include "ContentMusic.h"
#include "IGameDataLoader.h"
#include "StringEncodingTypes.h"
#include "RustInterface.h"

#include "rapidjson/document.h"
#include <string_theory/string>

#include <map>
#include <memory>
#include <stdexcept>
#include <vector>

class DefaultContentManager : public ContentManager, public IGameDataLoader
{
public:

	DefaultContentManager(GameVersion gameVersion,
				const ST::string &userHomeDir,
				const ST::string &gameResRootPath,
				const ST::string &externalizedDataPath);

	virtual ~DefaultContentManager() override;

	/// Called after construction.
	/// @throw runtime_error
	#ifdef __ANDROID__
	virtual void init(EngineOptions* engine_options, JNIEnv* jniEnv);
	#else
	virtual void init(EngineOptions* engine_options);
	#endif

	/** Load the game data. */
	bool loadGameData();

	/** Get map file path. */
	virtual ST::string getMapPath(const ST::string& mapName) const override;

	/** Get radar map resource name. */
	virtual ST::string getRadarMapResourceName(const ST::string &mapName) const override;

	/** Get tileset resource name. */
	virtual ST::string getTilesetResourceName(int number, ST::string fileName) const override;

	/** Get tileset db resource name. */
	virtual ST::string getTilesetDBResName() const override;

	/** Open map for reading. */
	virtual SGPFile* openMapForReading(const ST::string& mapName) const override;

	/** Get directory for storing new map file. */
	virtual ST::string getNewMapFolder() const override;

	/** Get all available maps. */
	virtual std::vector<ST::string> getAllMaps() const override;

	/** Get all available tilecache. */
	virtual std::vector<ST::string> getAllTilecache() const override;

	/* Open a game resource file for reading. */
	virtual SGPFile* openGameResForReading(const char* filename) const override;
	virtual SGPFile* openGameResForReading(const ST::string& filename) const override;

	/** Open temporary file for writing. */
	virtual SGPFile* openTempFileForWriting(const char* filename, bool truncate) const override;

	/** Open temporary file for reading. */
	virtual SGPFile* openTempFileForReading(const char* filename) const override;

	/** Open temporary file for appending. */
	virtual SGPFile* openTempFileForAppend(const char* filename) const override;

	/** Delete temporary file. */
	virtual void deleteTempFile(const char* filename) const override;

	/** Open user's private file (e.g. saved game, settings) for reading. */
	virtual SGPFile* openUserPrivateFileForReading(const ST::string& filename) const override;

	/* Checks if a game resource exists. */
	virtual bool doesGameResExists(char const* filename) const override;
	virtual bool doesGameResExists(const ST::string &filename) const override;

	/** Get folder for screenshots. */
	virtual ST::string getScreenshotFolder() const override;

	/** Get folder for video capture. */
	virtual ST::string getVideoCaptureFolder() const override;

	const ST::string& getDataDir() { return m_dataDir; }
	const ST::string& getTileDir() { return m_tileDir; }

	const ST::string& getExternalizedDataDir() { return m_externalizedDataPath; }

	/** Get folder for saved games. */
	virtual ST::string getSavedGamesFolder() const override;

	/** Load encrypted string from game resource file. */
	virtual ST::string loadEncryptedString(const char* fileName, uint32_t seek_chars, uint32_t read_chars) const override;

	virtual ST::string loadEncryptedString(SGPFile* File, uint32_t seek_chars, uint32_t read_chars) const override;

	/** Load dialogue quote from file. */
	virtual ST::string* loadDialogQuoteFromFile(const char* filename, int quote_number) override;

	/** Load all dialogue quotes for a character. */
	void loadAllDialogQuotes(STRING_ENC_TYPE encType, const char* filename, std::vector<ST::string*> &quotes) const;

	/** Get weapons with the give index. */
	virtual const WeaponModel* getWeapon(uint16_t index) override;
	virtual const WeaponModel* getWeaponByName(const ST::string &internalName) override;

	virtual const MagazineModel* getMagazineByName(const ST::string &internalName) override;
	virtual const MagazineModel* getMagazineByItemIndex(uint16_t itemIndex) override;
	virtual const std::vector<const MagazineModel*>& getMagazines() const override;

	virtual const CalibreModel* getCalibre(uint8_t index) override;
	virtual const ST::string* getCalibreName(uint8_t index) const override;
	virtual const ST::string* getCalibreNameForBobbyRay(uint8_t index) const override;

	virtual const AmmoTypeModel* getAmmoType(uint8_t index) override;

	virtual const ItemModel* getItem(uint16_t index) const override;
	virtual const ItemModel* getItemByName(const ST::string &internalName) const override;
	virtual const std::map<uint16_t, uint16_t> getMapItemReplacements() const override;

	virtual const std::vector<std::vector<const WeaponModel*> > & getNormalGunChoice() const override;
	virtual const std::vector<std::vector<const WeaponModel*> > & getExtendedGunChoice() const override;
	virtual const std::vector<GARRISON_GROUP>& getGarrisonGroups() const override;
	virtual const std::vector<PATROL_GROUP>& getPatrolGroups() const override;
	virtual const std::vector<ARMY_COMPOSITION>& getArmyCompositions() const override;

	virtual const DealerInventory* getDealerInventory(int dealerId) const override;
	virtual const DealerInventory* getBobbyRayNewInventory() const override;
	virtual const DealerInventory* getBobbyRayUsedInventory() const override;

	virtual const DealerModel* getDealer(uint8_t dealerID) const override;
	virtual const std::vector<const DealerModel*> getDealers() const override;

	virtual const std::vector<const ShippingDestinationModel*>& getShippingDestinations() const override;
	virtual const ShippingDestinationModel* getShippingDestination(uint8_t locationId) const override;
	virtual const ShippingDestinationModel* getPrimaryShippingDestination() const override;
	virtual const ST::string* getShippingDestinationName(uint8_t index) const override;

	virtual const NpcActionParamsModel* getNpcActionParams(uint16_t actionCode) const override;
	virtual const FactParamsModel* getFactParams(Fact fact) const override;

	virtual const ST::string* getMusicForMode(MusicMode mode) const override;

	virtual const GamePolicy* getGamePolicy() const override;
	virtual const IMPPolicy* getIMPPolicy() const override;

	virtual const ST::string* getNewString(size_t stringId) const override;

	virtual const std::vector<const BloodCatPlacementsModel*>& getBloodCatPlacements() const override;
	virtual const std::vector<const BloodCatSpawnsModel*>& getBloodCatSpawns() const override;
	virtual const BloodCatSpawnsModel* getBloodCatSpawnsOfSector(uint8_t sectorId) const override;
	virtual const std::vector<const CreatureLairModel*>& getCreatureLairs() const override;
	virtual const CreatureLairModel* getCreatureLair(uint8_t lairId) const override;
	virtual const CreatureLairModel* getCreatureLairByMineId(uint8_t mineId) const override;
	virtual const MineModel* getMineForSector(uint8_t sectorX, uint8_t sectorY, uint8_t sectorZ) const override;
	virtual const MineModel* getMine(uint8_t mineId) const override;
	virtual const std::vector<const MineModel*>& getMines() const override;
	virtual const std::vector<const SamSiteModel*>& getSamSites() const override;
	virtual const int8_t findSamIDBySector(uint8_t sectorId) const override;
	virtual const SamSiteModel* findSamSiteBySector(uint8_t sectorId) const override;
	virtual const int8_t getControllingSamSite(uint8_t sectorId) const override;
	virtual const TownModel* getTown(int8_t townId) const  override;
	virtual const std::map<int8_t, const TownModel*>& getTowns() const override;
	virtual const ST::string getTownName(uint8_t townId) const override;
	virtual const ST::string getTownLocative(uint8_t townId) const override;
	virtual const std::vector <const UndergroundSectorModel*>& getUndergroundSectors() const override;
	virtual const MovementCostsModel* getMovementCosts() const override;
	virtual const NpcPlacementModel* getNpcPlacement(uint8_t profileId) const override;
	virtual const RPCSmallFaceModel* getRPCSmallFaceOffsets(uint8_t profileID) const override;
	virtual const std::vector<const MERCListingModel*>& getMERCListings() const override;
	virtual const std::vector<const MercProfile*>& listMercProfiles() const override;
	virtual const LoadingScreen* getLoadingScreenForSector(uint8_t sectorId, uint8_t sectorLevel, bool isNight) const override;
	virtual const LoadingScreen* getLoadingScreen(uint8_t index) const override;

protected:
	ST::string m_dataDir;
	ST::string m_tileDir;
	ST::string m_userHomeDir;
	ST::string m_gameResRootPath;
	ST::string m_externalizedDataPath;

	const GameVersion m_gameVersion;

	std::vector<const ST::string*> m_newStrings;

	std::vector<const ItemModel*> m_items;
	std::vector<const MagazineModel*> m_magazines;

	std::vector<const CalibreModel*> m_calibres;
	std::vector<const ST::string*> m_calibreNames;
	std::vector<const ST::string*> m_calibreNamesBobbyRay;

	std::vector<AmmoTypeModel*> m_ammoTypes;

	/** Mapping of calibre names to objects. */
	std::map<ST::string, const AmmoTypeModel*> m_ammoTypeMap;
	std::map<ST::string, const CalibreModel*> m_calibreMap;
	std::map<ST::string, const MagazineModel*> m_magazineMap;
	std::map<ST::string, const WeaponModel*> m_weaponMap;
	std::map<ST::string, const ItemModel*> m_itemMap;
	std::map<uint16_t, uint16_t> m_mapItemReplacements;
	std::map<MusicMode, const std::vector<const ST::string*>*> m_musicMap;

	std::vector<std::vector<const WeaponModel*> > mNormalGunChoice;
	std::vector<std::vector<const WeaponModel*> > mExtendedGunChoice;
	std::vector<GARRISON_GROUP> m_garrisonGroups;
	std::vector<PATROL_GROUP> m_patrolGroups;
	std::vector<ARMY_COMPOSITION> m_armyCompositions;

	std::vector<const DealerInventory*> m_dealersInventory;
	const DealerInventory *m_bobbyRayNewInventory;
	const DealerInventory *m_bobbyRayUsedInventory;

	std::vector<const DealerModel*> m_dealers;

	std::vector<const ShippingDestinationModel*> m_shippingDestinations;
	std::vector<const ST::string*> m_shippingDestinationNames;

	std::map<Fact, const FactParamsModel*> m_factParams;
	std::map<uint16_t, const NpcActionParamsModel*> m_npcActionParams;
	std::map<uint8_t, const NpcPlacementModel*> m_npcPlacements;

	const IMPPolicy *m_impPolicy;
	const GamePolicy *m_gamePolicy;

	const LoadingScreenModel* m_loadingScreenModel;
	const MovementCostsModel* m_movementCosts;

	std::vector<const BloodCatPlacementsModel*> m_bloodCatPlacements;
	std::vector<const BloodCatSpawnsModel*> m_bloodCatSpawns;
	std::vector<const CreatureLairModel*> m_creatureLairs;
	std::vector<const MineModel*> m_mines;
	std::vector<const SamSiteModel*> m_samSites;
	const SamSiteAirControlModel* m_samSitesAirControl;
	std::map<int8_t, const TownModel*> m_towns;
	std::vector<const ST::string*> m_townNames;
	std::vector<const ST::string*> m_townNameLocatives;
	std::vector<const UndergroundSectorModel*> m_undergroundSectors;

	std::map<uint8_t, const RPCSmallFaceModel*> m_rpcSmallFaces;
	std::vector<const MERCListingModel*> m_MERCListings;

	// List of pre-constructed MercProfile objects; indices of elements are arbitrary (unlike gMercProfiles) and not guaranteed to follow any order
	std::vector<const MercProfile*> m_mercProfiles;
	std::map<uint8_t, const MercProfileInfo*> m_mercProfileInfo;

	RustPointer<Vfs> m_vfs;

	bool loadWeapons();
	bool loadMagazines();
	bool loadCalibres();
	bool loadAmmoTypes();
	bool loadArmyData();
	bool loadMusicModeList(MusicMode mode, rapidjson::Value &array);
	bool loadMusic();

	const DealerInventory * loadDealerInventory(const char *fileName);
	bool loadAllDealersAndInventory();
	void loadStringRes(const char *name, std::vector<const ST::string*> &strings) const;

	bool readWeaponTable(
		const char *fileName,
		std::vector<std::vector<const WeaponModel*> > & weaponTable);

	bool loadStrategicLayerData();
	bool loadTacticalLayerData();
	bool loadMercsData();

	std::unique_ptr<rapidjson::Document> readJsonDataFile(const char *fileName) const;

	/**
	 * @param profileID
	 * @return pointer to a MercProfileInfo. Never returns null. A pointer to the empty instance is returned if not defined in JSON.
	 */
	const MercProfileInfo* getMercProfileInfo(uint8_t profileID) const;
};

class LibraryFileNotFoundException : public std::runtime_error
{
public:
	LibraryFileNotFoundException(const ST::string& what_arg)
		:std::runtime_error(what_arg.to_std_string())
	{
	}
};
